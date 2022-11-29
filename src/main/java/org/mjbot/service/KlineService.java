package org.mjbot.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.apache.commons.collections4.map.HashedMap;
import org.mjbot.client.kucoin.builder.rest.KucoinRestBuilder;
import org.mjbot.client.kucoin.dto.rest.request.KlineRequestDTO;
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
    private final KucoinRestBuilder kucoinRestBuilder;
    int er;
    int suc;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<KlineRequestDTO>> failedRequestMaps = new ConcurrentHashMap<>();

    public KlineService(
        KlineRepository klineRepository,
        KlineMapper klineMapper,
        SymbolRepository symbolRepository,
        KucoinRestBuilder kucoinRestBuilder
    ) {
        this.klineRepository = klineRepository;
        this.klineMapper = klineMapper;
        this.symbolRepository = symbolRepository;
        this.kucoinRestBuilder = kucoinRestBuilder;
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

    @Scheduled(cron = "2 * * * * *")
    public void getKlineFromServer() {
        ZonedDateTime now = ZonedDateTime.now();
        now = now.minusNanos(now.getNano());
        now = now.minusSeconds(now.getSecond());
        ZonedDateTime preOneMin = now.minusMinutes(1);

        long time = now.toEpochSecond();
        long preTime = preOneMin.toEpochSecond();

        List<Symbol> active = symbolRepository.findAllByActive(true);
        for (Symbol symbol : active) {
            KlineRequestDTO request = new KlineRequestDTO()
                .setSymbol(symbol.getSymbol())
                .setType("1min")
                .setStartAt(preTime)
                .setEndAt(time);
            try {
                List<List<String>> klines = kucoinRestBuilder.getKline(request, new HashedMap<>());
                if (!klines.isEmpty()) {
                    klines.forEach(strings -> {
                        Kline kline = new Kline()
                            .time(Long.valueOf(strings.get(0)))
                            .open(strings.get(1))
                            .close(strings.get(2))
                            .high(strings.get(3))
                            .low(strings.get(4))
                            .volume(strings.get(5))
                            .turnover(strings.get(6))
                            .timeType("1min")
                            .symbol(symbol);
                        klineRepository.save(kline);
                    });
                    suc++;
                } else {
                    Kline lastKline = klineRepository.findFirstBySymbol_IdAndTimeTypeOrderByTimeDesc(symbol.getId(), "1min");
                    Kline kline = klineMapper.clone(lastKline);
                    kline.setId(null);
                    kline.setSymbol(symbol);
                    kline.setTime(preTime);

                    klineRepository.save(kline);
                }
            } catch (Exception e) {
                er++;
                if (failedRequestMaps.containsKey(symbol.getSymbol())) {
                    List<KlineRequestDTO> klineRequestDTOS = failedRequestMaps.get(symbol.getSymbol());
                    klineRequestDTOS.add(request);
                } else {
                    CopyOnWriteArrayList<KlineRequestDTO> klineRequestDTOS = new CopyOnWriteArrayList<>();
                    klineRequestDTOS.add(request);
                    failedRequestMaps.put(symbol.getSymbol(), klineRequestDTOS);
                }
            }
        }
        log.debug(String.valueOf(suc));
        log.debug(String.valueOf(er));
    }

    @Scheduled(cron = "2 */5 * * * *")
    public void getKlineFromServer5Min() {
        ZonedDateTime now = ZonedDateTime.now();
        now = now.minusNanos(now.getNano());
        now = now.minusSeconds(now.getSecond());
        ZonedDateTime preFiveMin = now.minusMinutes(5);

        long time = now.toEpochSecond();
        long preTime = preFiveMin.toEpochSecond();

        List<Symbol> active = symbolRepository.findAllByActive(true);
        for (Symbol symbol : active) {
            KlineRequestDTO request = new KlineRequestDTO()
                .setSymbol(symbol.getSymbol())
                .setType("5min")
                .setStartAt(preTime)
                .setEndAt(time);
            try {
                List<List<String>> klines = kucoinRestBuilder.getKline(request, new HashedMap<>());
                if (!klines.isEmpty()) {
                    klines.forEach(strings -> {
                        Kline kline = new Kline()
                            .time(Long.valueOf(strings.get(0)))
                            .open(strings.get(1))
                            .close(strings.get(2))
                            .high(strings.get(3))
                            .low(strings.get(4))
                            .volume(strings.get(5))
                            .turnover(strings.get(6))
                            .timeType("5min")
                            .symbol(symbol);
                        klineRepository.save(kline);
                    });
                    suc++;
                } else {
                    Kline lastKline = klineRepository.findFirstBySymbol_IdAndTimeTypeOrderByTimeDesc(symbol.getId(), "5min");
                    Kline kline = klineMapper.clone(lastKline);
                    kline.setId(null);
                    kline.setSymbol(symbol);
                    kline.setTime(preTime);

                    klineRepository.save(kline);
                }
            } catch (Exception e) {
                er++;
                if (failedRequestMaps.containsKey(symbol.getSymbol())) {
                    List<KlineRequestDTO> klineRequestDTOS = failedRequestMaps.get(symbol.getSymbol());
                    klineRequestDTOS.add(request);
                } else {
                    CopyOnWriteArrayList<KlineRequestDTO> klineRequestDTOS = new CopyOnWriteArrayList<>();
                    klineRequestDTOS.add(request);
                    failedRequestMaps.put(symbol.getSymbol(), klineRequestDTOS);
                }
            }
        }
        log.debug(String.valueOf(suc));
        log.debug(String.valueOf(er));
    }

    @Scheduled(cron = "2 */15 * * * *")
    public void getKlineFromServer15Min() {
        ZonedDateTime now = ZonedDateTime.now();
        now = now.minusNanos(now.getNano());
        now = now.minusSeconds(now.getSecond());
        ZonedDateTime preFiveMin = now.minusMinutes(15);

        long time = now.toEpochSecond();
        long preTime = preFiveMin.toEpochSecond();

        List<Symbol> active = symbolRepository.findAllByActive(true);
        for (Symbol symbol : active) {
            KlineRequestDTO request = new KlineRequestDTO()
                .setSymbol(symbol.getSymbol())
                .setType("15min")
                .setStartAt(preTime)
                .setEndAt(time);
            try {
                List<List<String>> klines = kucoinRestBuilder.getKline(request, new HashedMap<>());
                if (!klines.isEmpty()) {
                    klines.forEach(strings -> {
                        Kline kline = new Kline()
                            .time(Long.valueOf(strings.get(0)))
                            .open(strings.get(1))
                            .close(strings.get(2))
                            .high(strings.get(3))
                            .low(strings.get(4))
                            .volume(strings.get(5))
                            .turnover(strings.get(6))
                            .timeType("15min")
                            .symbol(symbol);
                        klineRepository.save(kline);
                    });
                    suc++;
                } else {
                    Kline lastKline = klineRepository.findFirstBySymbol_IdAndTimeTypeOrderByTimeDesc(symbol.getId(), "15min");
                    if (lastKline != null) {
                        Kline kline = klineMapper.clone(lastKline);
                        kline.setId(null);
                        kline.setSymbol(symbol);
                        kline.setTime(preTime);

                        klineRepository.save(kline);
                    }
                }
            } catch (Exception e) {
                er++;
                if (failedRequestMaps.containsKey(symbol.getSymbol())) {
                    List<KlineRequestDTO> klineRequestDTOS = failedRequestMaps.get(symbol.getSymbol());
                    klineRequestDTOS.add(request);
                } else {
                    CopyOnWriteArrayList<KlineRequestDTO> klineRequestDTOS = new CopyOnWriteArrayList<>();
                    klineRequestDTOS.add(request);
                    failedRequestMaps.put(symbol.getSymbol(), klineRequestDTOS);
                }
            }
        }
        log.debug(String.valueOf(suc));
        log.debug(String.valueOf(er));
    }

    @Scheduled(cron = "*/15 * * * * *")
    public void doFailedRequests() {
        failedRequestMaps.forEach((symbol, requests) -> {
            for (KlineRequestDTO request : requests) {
                log.debug("re-request for {} failed request.", symbol);
                try {
                    List<List<String>> klines = kucoinRestBuilder.getKline(request, new HashedMap<>());
                    if (!klines.isEmpty()) {
                        klines.forEach(strings -> {
                            Kline kline = new Kline()
                                .time(Long.valueOf(strings.get(0)))
                                .open(strings.get(1))
                                .close(strings.get(2))
                                .high(strings.get(3))
                                .low(strings.get(4))
                                .volume(strings.get(5))
                                .turnover(strings.get(6))
                                .timeType("1min")
                                .symbol(symbolRepository.findFirstBySymbol(symbol));
                            klineRepository.save(kline);
                            requests.remove(request);
                        });
                        er--;
                        suc++;
                    } else {
                        requests.remove(request);
                        er--;
                        suc++;
                    }
                } catch (Exception e) {}
            }
        });
        log.debug(String.valueOf(suc));
        log.debug(String.valueOf(er));
    }
}
