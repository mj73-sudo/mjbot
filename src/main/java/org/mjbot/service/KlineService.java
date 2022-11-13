package org.mjbot.service;

import java.util.Optional;
import org.mjbot.domain.Kline;
import org.mjbot.repository.KlineRepository;
import org.mjbot.service.dto.KlineDTO;
import org.mjbot.service.mapper.KlineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public KlineService(KlineRepository klineRepository, KlineMapper klineMapper) {
        this.klineRepository = klineRepository;
        this.klineMapper = klineMapper;
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
}
