package org.mjbot.service;

import com.kucoin.sdk.KucoinRestClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public KlineService(
        KlineRepository klineRepository,
        KlineMapper klineMapper,
        SymbolRepository symbolRepository,
        KucoinRestClient kucoinRestClient
    ) {
        this.klineRepository = klineRepository;
        this.klineMapper = klineMapper;
        this.symbolRepository = symbolRepository;
        this.kucoinRestClient = kucoinRestClient;
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

    @Scheduled(cron = "0 */5 * * * *")
    public void getKlinesEvery5Min() {
        List<Symbol> actives = symbolRepository.findAllByActive(true);
        for (Symbol symbol : actives) {
            Kline lastKline = klineRepository.findFirstBySymbol_IdAndTimeTypeOrderByTimeDesc(symbol.getId(), "5min");
            try {
                Thread.sleep(5000);
                List<Kline> klines = new ArrayList<>();
                List<List<String>> historicRates = restClient
                    .historyAPI()
                    .getHistoricRates(symbol.getSymbol(), lastKline != null ? lastKline.getTime() : 0, 0, "1min");
                historicRates.forEach(strings -> {
                    Kline kline = klineRepository.findFirstByTimeAndTimeTypeAndSymbol_Id(
                        Long.valueOf(strings.get(0)),
                        "1min",
                        symbol.getId()
                    );
                    if (kline == null) {
                        kline = new Kline();
                    }
                    kline =
                        kline
                            .time(Long.valueOf(strings.get(0)))
                            .open(strings.get(1))
                            .close(strings.get(2))
                            .high(strings.get(3))
                            .low(strings.get(4))
                            .volume(strings.get(5))
                            .turnover(strings.get(6))
                            .timeType("1min")
                            .symbol(symbol);
                    klines.add(kline);
                });
                klineRepository.saveAllAndFlush(klines);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Async
    public void saveToDb(List<Kline> klines) {
        klineRepository.saveAllAndFlush(klines);
    }
}
