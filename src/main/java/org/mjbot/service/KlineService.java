package org.mjbot.service;

import static org.mjbot.client.kucoin.builder.ws.KucoinWsBuilder.wsPublicToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kucoin.sdk.KucoinRestClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.map.HashedMap;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.mjbot.client.kucoin.builder.ObjectMapperBuilder;
import org.mjbot.client.kucoin.builder.ws.WebSocketListener;
import org.mjbot.client.kucoin.dto.ws.request.WsRequestDTO;
import org.mjbot.client.kucoin.dto.ws.response.BaseWsResponseDTO;
import org.mjbot.domain.Kline;
import org.mjbot.domain.Symbol;
import org.mjbot.repository.KlineRepository;
import org.mjbot.repository.SymbolRepository;
import org.mjbot.service.dto.KlineDTO;
import org.mjbot.service.mapper.KlineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Kline}.
 */
@Service
@Transactional
public class KlineService {

    private final Logger log = LoggerFactory.getLogger(KlineService.class);

    private final KlineRepository klineRepository;

    private final KlineMapper klineMapper;

    private final SymbolRepository symbolRepository;

    private final KucoinRestClient kucoinRestClient;

    private final Map<String, List<List<String>>> klines = new HashedMap<>();

    private final InfluxDB influxDB;

    public KlineService(
        KlineRepository klineRepository,
        KlineMapper klineMapper,
        SymbolRepository symbolRepository,
        KucoinRestClient kucoinRestClient,
        InfluxDB influxDB
    ) {
        this.klineRepository = klineRepository;
        this.klineMapper = klineMapper;
        this.symbolRepository = symbolRepository;
        this.kucoinRestClient = kucoinRestClient;
        this.influxDB = influxDB;
    }

    /**
     * Save a kline.
     *
     * @param klineDTO the entity to save.
     * @return the persisted entity.
     */
    public KlineDTO save(KlineDTO klineDTO) {
        log.debug("Request to save Kline : {}", klineDTO);
        Kline kline = klineMapper.toEntity(klineDTO);
        kline = klineRepository.save(kline);
        return klineMapper.toDto(kline);
    }

    /**
     * Update a kline.
     *
     * @param klineDTO the entity to save.
     * @return the persisted entity.
     */
    public KlineDTO update(KlineDTO klineDTO) {
        log.debug("Request to update Kline : {}", klineDTO);
        Kline kline = klineMapper.toEntity(klineDTO);
        kline = klineRepository.save(kline);
        return klineMapper.toDto(kline);
    }

    /**
     * Partially update a kline.
     *
     * @param klineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<KlineDTO> partialUpdate(KlineDTO klineDTO) {
        log.debug("Request to partially update Kline : {}", klineDTO);

        return klineRepository
            .findById(klineDTO.getId())
            .map(existingKline -> {
                klineMapper.partialUpdate(existingKline, klineDTO);

                return existingKline;
            })
            .map(klineRepository::save)
            .map(klineMapper::toDto);
    }

    /**
     * Get all the klines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<KlineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Klines");
        return klineRepository.findAll(pageable).map(klineMapper::toDto);
    }

    /**
     * Get all the klines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<KlineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return klineRepository.findAllWithEagerRelationships(pageable).map(klineMapper::toDto);
    }

    /**
     * Get one kline by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<KlineDTO> findOne(Long id) {
        log.debug("Request to get Kline : {}", id);
        return klineRepository.findOneWithEagerRelationships(id).map(klineMapper::toDto);
    }

    /**
     * Delete the kline by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Kline : {}", id);
        klineRepository.deleteById(id);
    }

    //    @Scheduled(cron = "0 */5 * * * *")
    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void getKlinesEvery5Min() {
        List<Symbol> actives = symbolRepository.findAllByActive(true);
        //        ExecutorService service = Executors.newFixedThreadPool(actives.size());
        ExecutorService service = Executors.newCachedThreadPool();
        for (Symbol symbol : actives) {
            service.execute(() -> {
                String hostName = "wss://ws-api-spot.kucoin.com" + "?token=" + wsPublicToken;
                hostName = hostName.replace("endpoint", "");
                WsRequestDTO wsRequestDTO = new WsRequestDTO();
                wsRequestDTO.setId(Instant.now().getEpochSecond());
                wsRequestDTO.setPrivateChannel(false);
                wsRequestDTO.setResponse(true);
                wsRequestDTO.setType("subscribe");
                wsRequestDTO.setTopic("/market/candles:" + symbol.getSymbol() + "_1min");
                WebSocket ws = HttpClient
                    .newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(
                        URI.create(hostName),
                        new WebSocketListener(
                            hostName,
                            wsRequestDTO,
                            text -> {
                                ObjectMapper instance = ObjectMapperBuilder.getInstance();
                                BaseWsResponseDTO baseWsResponseDTO = instance.readValue(text, BaseWsResponseDTO.class);
                                if (klines.containsKey(symbol.getSymbol())) {
                                    List<List<String>> candles = klines.get(symbol.getSymbol());
                                    candles.add((List<String>) baseWsResponseDTO.getData().get("candles"));
                                } else {
                                    List<List<String>> candles = Collections.synchronizedList(new ArrayList<>());
                                    candles.add((List<String>) baseWsResponseDTO.getData().get("candles"));
                                    klines.put(symbol.getSymbol(), candles);
                                }
                                //                                log.debug(text);
                            }
                        )
                    )
                    .join();
            });
        }
    }

    @Scheduled(cron = "2 * * * * *")
    public void saveCandleEvery1Min() {
        List<Kline> klineList = new ArrayList<>();
        ZonedDateTime preCandle = ZonedDateTime.now();
        preCandle = preCandle.minusSeconds(preCandle.getSecond());
        preCandle = preCandle.minusNanos(preCandle.getNano());
        preCandle = preCandle.minusMinutes(1);

        long preTimeStamp = preCandle.toEpochSecond();

        klines
            .entrySet()
            .forEach(map -> {
                Symbol symbol = symbolRepository.findFirstBySymbol(map.getKey());
                List<List<String>> candles = map
                    .getValue()
                    .stream()
                    .filter(strings -> strings.get(0).equalsIgnoreCase(String.valueOf(preTimeStamp)))
                    .collect(Collectors.toList());
                if (!candles.isEmpty()) {
                    List<String> strings = candles.get(candles.size() - 1);
                    Point point = Point
                        .measurement(symbol.getSymbol())
                        .time(Long.valueOf(strings.get(0)), TimeUnit.MILLISECONDS)
                        .addField("open", strings.get(1))
                        .addField("close", strings.get(2))
                        .addField("high", strings.get(3))
                        .addField("low", strings.get(4))
                        .addField("volume", strings.get(5))
                        .addField("turnover", strings.get(6))
                        .build();
                    Kline kline = new Kline()
                        .time(Long.valueOf(strings.get(0)))
                        .open(strings.get(1))
                        .close(strings.get(2))
                        .high(strings.get(3))
                        .low(strings.get(4))
                        .volume(strings.get(5))
                        .turnover(strings.get(6))
                        .timeType("1min")
                        .symbol(symbolRepository.findFirstBySymbol(map.getKey()));

                    klineList.add(kline);
                    influxDB.setDatabase("mjbot").write(point);
                }
                List<List<String>> newCandles = map
                    .getValue()
                    .stream()
                    .filter(strings -> !strings.get(0).equalsIgnoreCase(String.valueOf(preTimeStamp)))
                    .collect(Collectors.toList());
                map.setValue(Collections.synchronizedList(newCandles));
            });
        klineRepository.saveAll(klineList);
    }
}
