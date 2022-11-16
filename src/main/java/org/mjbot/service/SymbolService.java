package org.mjbot.service;

import com.kucoin.sdk.KucoinRestClient;
import com.kucoin.sdk.rest.response.SymbolResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mjbot.domain.Symbol;
import org.mjbot.repository.SymbolRepository;
import org.mjbot.service.dto.SymbolDTO;
import org.mjbot.service.mapper.SymbolMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Symbol}.
 */
@Service
@Transactional
public class SymbolService {

    private final Logger log = LoggerFactory.getLogger(SymbolService.class);

    private final SymbolRepository symbolRepository;

    private final SymbolMapper symbolMapper;

    private final KucoinRestClient kucoinRestClient;

    public SymbolService(SymbolRepository symbolRepository, SymbolMapper symbolMapper, KucoinRestClient kucoinRestClient) {
        this.symbolRepository = symbolRepository;
        this.symbolMapper = symbolMapper;
        this.kucoinRestClient = kucoinRestClient;
    }

    /**
     * Save a symbol.
     *
     * @param symbolDTO the entity to save.
     * @return the persisted entity.
     */
    public SymbolDTO save(SymbolDTO symbolDTO) {
        log.debug("Request to save Symbol : {}", symbolDTO);
        Symbol symbol = symbolMapper.toEntity(symbolDTO);
        symbol = symbolRepository.save(symbol);
        return symbolMapper.toDto(symbol);
    }

    /**
     * Update a symbol.
     *
     * @param symbolDTO the entity to save.
     * @return the persisted entity.
     */
    public SymbolDTO update(SymbolDTO symbolDTO) {
        log.debug("Request to update Symbol : {}", symbolDTO);
        Symbol symbol = symbolMapper.toEntity(symbolDTO);
        symbol = symbolRepository.save(symbol);
        return symbolMapper.toDto(symbol);
    }

    /**
     * Partially update a symbol.
     *
     * @param symbolDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SymbolDTO> partialUpdate(SymbolDTO symbolDTO) {
        log.debug("Request to partially update Symbol : {}", symbolDTO);

        return symbolRepository
            .findById(symbolDTO.getId())
            .map(existingSymbol -> {
                symbolMapper.partialUpdate(existingSymbol, symbolDTO);

                return existingSymbol;
            })
            .map(symbolRepository::save)
            .map(symbolMapper::toDto);
    }

    /**
     * Get all the symbols.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SymbolDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Symbols");
        return symbolRepository.findAll(pageable).map(symbolMapper::toDto);
    }

    /**
     * Get one symbol by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SymbolDTO> findOne(Long id) {
        log.debug("Request to get Symbol : {}", id);
        return symbolRepository.findById(id).map(symbolMapper::toDto);
    }

    /**
     * Delete the symbol by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Symbol : {}", id);
        symbolRepository.deleteById(id);
    }

    //    @Scheduled(initialDelay = 1, fixedRate = 1000 * 60 * 60 * 24 * 7)
    public void getSymbols() throws IOException {
        List<SymbolResponse> response = kucoinRestClient.symbolAPI().getSymbols();
        List<Symbol> symbols = response
            .stream()
            .filter(res -> {
                if (res.getQuoteCurrency().equalsIgnoreCase("usdt")) {
                    boolean b = symbolRepository.existsAllBySymbol(res.getSymbol());
                    return !b;
                } else {
                    return false;
                }
            })
            .map(symbolMapper::responseToSymbol)
            .collect(Collectors.toList());
        symbolRepository.saveAll(symbols);
    }
}
