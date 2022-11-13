package org.mjbot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mjbot.IntegrationTest;
import org.mjbot.domain.Symbol;
import org.mjbot.repository.SymbolRepository;
import org.mjbot.service.criteria.SymbolCriteria;
import org.mjbot.service.dto.SymbolDTO;
import org.mjbot.service.mapper.SymbolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SymbolResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SymbolResourceIT {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BASE_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_BASE_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_QUOTE_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_FEE_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_FEE_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_MARKET = "AAAAAAAAAA";
    private static final String UPDATED_MARKET = "BBBBBBBBBB";

    private static final String DEFAULT_BASE_MIN_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_BASE_MIN_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_QUOTE_MIN_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_MIN_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_BASE_MAX_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_BASE_MAX_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_QUOTE_MAX_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_MAX_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_BASE_INCREMENT = "AAAAAAAAAA";
    private static final String UPDATED_BASE_INCREMENT = "BBBBBBBBBB";

    private static final String DEFAULT_QUOTE_INCREMENT = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_INCREMENT = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE_INCREMENT = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_INCREMENT = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE_LIMIT_RATE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_LIMIT_RATE = "BBBBBBBBBB";

    private static final String DEFAULT_MIN_FUNDS = "AAAAAAAAAA";
    private static final String UPDATED_MIN_FUNDS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_MARGIN_ENABLED = false;
    private static final Boolean UPDATED_IS_MARGIN_ENABLED = true;

    private static final Boolean DEFAULT_ENABLE_TRADING = false;
    private static final Boolean UPDATED_ENABLE_TRADING = true;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/symbols";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private SymbolMapper symbolMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSymbolMockMvc;

    private Symbol symbol;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Symbol createEntity(EntityManager em) {
        Symbol symbol = new Symbol()
            .symbol(DEFAULT_SYMBOL)
            .name(DEFAULT_NAME)
            .baseCurrency(DEFAULT_BASE_CURRENCY)
            .quoteCurrency(DEFAULT_QUOTE_CURRENCY)
            .feeCurrency(DEFAULT_FEE_CURRENCY)
            .market(DEFAULT_MARKET)
            .baseMinSize(DEFAULT_BASE_MIN_SIZE)
            .quoteMinSize(DEFAULT_QUOTE_MIN_SIZE)
            .baseMaxSize(DEFAULT_BASE_MAX_SIZE)
            .quoteMaxSize(DEFAULT_QUOTE_MAX_SIZE)
            .baseIncrement(DEFAULT_BASE_INCREMENT)
            .quoteIncrement(DEFAULT_QUOTE_INCREMENT)
            .priceIncrement(DEFAULT_PRICE_INCREMENT)
            .priceLimitRate(DEFAULT_PRICE_LIMIT_RATE)
            .minFunds(DEFAULT_MIN_FUNDS)
            .isMarginEnabled(DEFAULT_IS_MARGIN_ENABLED)
            .enableTrading(DEFAULT_ENABLE_TRADING)
            .active(DEFAULT_ACTIVE);
        return symbol;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Symbol createUpdatedEntity(EntityManager em) {
        Symbol symbol = new Symbol()
            .symbol(UPDATED_SYMBOL)
            .name(UPDATED_NAME)
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .feeCurrency(UPDATED_FEE_CURRENCY)
            .market(UPDATED_MARKET)
            .baseMinSize(UPDATED_BASE_MIN_SIZE)
            .quoteMinSize(UPDATED_QUOTE_MIN_SIZE)
            .baseMaxSize(UPDATED_BASE_MAX_SIZE)
            .quoteMaxSize(UPDATED_QUOTE_MAX_SIZE)
            .baseIncrement(UPDATED_BASE_INCREMENT)
            .quoteIncrement(UPDATED_QUOTE_INCREMENT)
            .priceIncrement(UPDATED_PRICE_INCREMENT)
            .priceLimitRate(UPDATED_PRICE_LIMIT_RATE)
            .minFunds(UPDATED_MIN_FUNDS)
            .isMarginEnabled(UPDATED_IS_MARGIN_ENABLED)
            .enableTrading(UPDATED_ENABLE_TRADING)
            .active(UPDATED_ACTIVE);
        return symbol;
    }

    @BeforeEach
    public void initTest() {
        symbol = createEntity(em);
    }

    @Test
    @Transactional
    void createSymbol() throws Exception {
        int databaseSizeBeforeCreate = symbolRepository.findAll().size();
        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);
        restSymbolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(symbolDTO)))
            .andExpect(status().isCreated());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeCreate + 1);
        Symbol testSymbol = symbolList.get(symbolList.size() - 1);
        assertThat(testSymbol.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testSymbol.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSymbol.getBaseCurrency()).isEqualTo(DEFAULT_BASE_CURRENCY);
        assertThat(testSymbol.getQuoteCurrency()).isEqualTo(DEFAULT_QUOTE_CURRENCY);
        assertThat(testSymbol.getFeeCurrency()).isEqualTo(DEFAULT_FEE_CURRENCY);
        assertThat(testSymbol.getMarket()).isEqualTo(DEFAULT_MARKET);
        assertThat(testSymbol.getBaseMinSize()).isEqualTo(DEFAULT_BASE_MIN_SIZE);
        assertThat(testSymbol.getQuoteMinSize()).isEqualTo(DEFAULT_QUOTE_MIN_SIZE);
        assertThat(testSymbol.getBaseMaxSize()).isEqualTo(DEFAULT_BASE_MAX_SIZE);
        assertThat(testSymbol.getQuoteMaxSize()).isEqualTo(DEFAULT_QUOTE_MAX_SIZE);
        assertThat(testSymbol.getBaseIncrement()).isEqualTo(DEFAULT_BASE_INCREMENT);
        assertThat(testSymbol.getQuoteIncrement()).isEqualTo(DEFAULT_QUOTE_INCREMENT);
        assertThat(testSymbol.getPriceIncrement()).isEqualTo(DEFAULT_PRICE_INCREMENT);
        assertThat(testSymbol.getPriceLimitRate()).isEqualTo(DEFAULT_PRICE_LIMIT_RATE);
        assertThat(testSymbol.getMinFunds()).isEqualTo(DEFAULT_MIN_FUNDS);
        assertThat(testSymbol.getIsMarginEnabled()).isEqualTo(DEFAULT_IS_MARGIN_ENABLED);
        assertThat(testSymbol.getEnableTrading()).isEqualTo(DEFAULT_ENABLE_TRADING);
        assertThat(testSymbol.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createSymbolWithExistingId() throws Exception {
        // Create the Symbol with an existing ID
        symbol.setId(1L);
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        int databaseSizeBeforeCreate = symbolRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSymbolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(symbolDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = symbolRepository.findAll().size();
        // set the field null
        symbol.setSymbol(null);

        // Create the Symbol, which fails.
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        restSymbolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(symbolDTO)))
            .andExpect(status().isBadRequest());

        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = symbolRepository.findAll().size();
        // set the field null
        symbol.setName(null);

        // Create the Symbol, which fails.
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        restSymbolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(symbolDTO)))
            .andExpect(status().isBadRequest());

        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSymbols() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symbol.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].baseCurrency").value(hasItem(DEFAULT_BASE_CURRENCY)))
            .andExpect(jsonPath("$.[*].quoteCurrency").value(hasItem(DEFAULT_QUOTE_CURRENCY)))
            .andExpect(jsonPath("$.[*].feeCurrency").value(hasItem(DEFAULT_FEE_CURRENCY)))
            .andExpect(jsonPath("$.[*].market").value(hasItem(DEFAULT_MARKET)))
            .andExpect(jsonPath("$.[*].baseMinSize").value(hasItem(DEFAULT_BASE_MIN_SIZE)))
            .andExpect(jsonPath("$.[*].quoteMinSize").value(hasItem(DEFAULT_QUOTE_MIN_SIZE)))
            .andExpect(jsonPath("$.[*].baseMaxSize").value(hasItem(DEFAULT_BASE_MAX_SIZE)))
            .andExpect(jsonPath("$.[*].quoteMaxSize").value(hasItem(DEFAULT_QUOTE_MAX_SIZE)))
            .andExpect(jsonPath("$.[*].baseIncrement").value(hasItem(DEFAULT_BASE_INCREMENT)))
            .andExpect(jsonPath("$.[*].quoteIncrement").value(hasItem(DEFAULT_QUOTE_INCREMENT)))
            .andExpect(jsonPath("$.[*].priceIncrement").value(hasItem(DEFAULT_PRICE_INCREMENT)))
            .andExpect(jsonPath("$.[*].priceLimitRate").value(hasItem(DEFAULT_PRICE_LIMIT_RATE)))
            .andExpect(jsonPath("$.[*].minFunds").value(hasItem(DEFAULT_MIN_FUNDS)))
            .andExpect(jsonPath("$.[*].isMarginEnabled").value(hasItem(DEFAULT_IS_MARGIN_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].enableTrading").value(hasItem(DEFAULT_ENABLE_TRADING.booleanValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getSymbol() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get the symbol
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL_ID, symbol.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(symbol.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.baseCurrency").value(DEFAULT_BASE_CURRENCY))
            .andExpect(jsonPath("$.quoteCurrency").value(DEFAULT_QUOTE_CURRENCY))
            .andExpect(jsonPath("$.feeCurrency").value(DEFAULT_FEE_CURRENCY))
            .andExpect(jsonPath("$.market").value(DEFAULT_MARKET))
            .andExpect(jsonPath("$.baseMinSize").value(DEFAULT_BASE_MIN_SIZE))
            .andExpect(jsonPath("$.quoteMinSize").value(DEFAULT_QUOTE_MIN_SIZE))
            .andExpect(jsonPath("$.baseMaxSize").value(DEFAULT_BASE_MAX_SIZE))
            .andExpect(jsonPath("$.quoteMaxSize").value(DEFAULT_QUOTE_MAX_SIZE))
            .andExpect(jsonPath("$.baseIncrement").value(DEFAULT_BASE_INCREMENT))
            .andExpect(jsonPath("$.quoteIncrement").value(DEFAULT_QUOTE_INCREMENT))
            .andExpect(jsonPath("$.priceIncrement").value(DEFAULT_PRICE_INCREMENT))
            .andExpect(jsonPath("$.priceLimitRate").value(DEFAULT_PRICE_LIMIT_RATE))
            .andExpect(jsonPath("$.minFunds").value(DEFAULT_MIN_FUNDS))
            .andExpect(jsonPath("$.isMarginEnabled").value(DEFAULT_IS_MARGIN_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.enableTrading").value(DEFAULT_ENABLE_TRADING.booleanValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getSymbolsByIdFiltering() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        Long id = symbol.getId();

        defaultSymbolShouldBeFound("id.equals=" + id);
        defaultSymbolShouldNotBeFound("id.notEquals=" + id);

        defaultSymbolShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSymbolShouldNotBeFound("id.greaterThan=" + id);

        defaultSymbolShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSymbolShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSymbolsBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where symbol equals to DEFAULT_SYMBOL
        defaultSymbolShouldBeFound("symbol.equals=" + DEFAULT_SYMBOL);

        // Get all the symbolList where symbol equals to UPDATED_SYMBOL
        defaultSymbolShouldNotBeFound("symbol.equals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllSymbolsBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultSymbolShouldBeFound("symbol.in=" + DEFAULT_SYMBOL + "," + UPDATED_SYMBOL);

        // Get all the symbolList where symbol equals to UPDATED_SYMBOL
        defaultSymbolShouldNotBeFound("symbol.in=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllSymbolsBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where symbol is not null
        defaultSymbolShouldBeFound("symbol.specified=true");

        // Get all the symbolList where symbol is null
        defaultSymbolShouldNotBeFound("symbol.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsBySymbolContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where symbol contains DEFAULT_SYMBOL
        defaultSymbolShouldBeFound("symbol.contains=" + DEFAULT_SYMBOL);

        // Get all the symbolList where symbol contains UPDATED_SYMBOL
        defaultSymbolShouldNotBeFound("symbol.contains=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllSymbolsBySymbolNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where symbol does not contain DEFAULT_SYMBOL
        defaultSymbolShouldNotBeFound("symbol.doesNotContain=" + DEFAULT_SYMBOL);

        // Get all the symbolList where symbol does not contain UPDATED_SYMBOL
        defaultSymbolShouldBeFound("symbol.doesNotContain=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllSymbolsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where name equals to DEFAULT_NAME
        defaultSymbolShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the symbolList where name equals to UPDATED_NAME
        defaultSymbolShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSymbolsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSymbolShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the symbolList where name equals to UPDATED_NAME
        defaultSymbolShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSymbolsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where name is not null
        defaultSymbolShouldBeFound("name.specified=true");

        // Get all the symbolList where name is null
        defaultSymbolShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByNameContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where name contains DEFAULT_NAME
        defaultSymbolShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the symbolList where name contains UPDATED_NAME
        defaultSymbolShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSymbolsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where name does not contain DEFAULT_NAME
        defaultSymbolShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the symbolList where name does not contain UPDATED_NAME
        defaultSymbolShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseCurrency equals to DEFAULT_BASE_CURRENCY
        defaultSymbolShouldBeFound("baseCurrency.equals=" + DEFAULT_BASE_CURRENCY);

        // Get all the symbolList where baseCurrency equals to UPDATED_BASE_CURRENCY
        defaultSymbolShouldNotBeFound("baseCurrency.equals=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseCurrency in DEFAULT_BASE_CURRENCY or UPDATED_BASE_CURRENCY
        defaultSymbolShouldBeFound("baseCurrency.in=" + DEFAULT_BASE_CURRENCY + "," + UPDATED_BASE_CURRENCY);

        // Get all the symbolList where baseCurrency equals to UPDATED_BASE_CURRENCY
        defaultSymbolShouldNotBeFound("baseCurrency.in=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseCurrency is not null
        defaultSymbolShouldBeFound("baseCurrency.specified=true");

        // Get all the symbolList where baseCurrency is null
        defaultSymbolShouldNotBeFound("baseCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseCurrencyContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseCurrency contains DEFAULT_BASE_CURRENCY
        defaultSymbolShouldBeFound("baseCurrency.contains=" + DEFAULT_BASE_CURRENCY);

        // Get all the symbolList where baseCurrency contains UPDATED_BASE_CURRENCY
        defaultSymbolShouldNotBeFound("baseCurrency.contains=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseCurrency does not contain DEFAULT_BASE_CURRENCY
        defaultSymbolShouldNotBeFound("baseCurrency.doesNotContain=" + DEFAULT_BASE_CURRENCY);

        // Get all the symbolList where baseCurrency does not contain UPDATED_BASE_CURRENCY
        defaultSymbolShouldBeFound("baseCurrency.doesNotContain=" + UPDATED_BASE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteCurrency equals to DEFAULT_QUOTE_CURRENCY
        defaultSymbolShouldBeFound("quoteCurrency.equals=" + DEFAULT_QUOTE_CURRENCY);

        // Get all the symbolList where quoteCurrency equals to UPDATED_QUOTE_CURRENCY
        defaultSymbolShouldNotBeFound("quoteCurrency.equals=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteCurrency in DEFAULT_QUOTE_CURRENCY or UPDATED_QUOTE_CURRENCY
        defaultSymbolShouldBeFound("quoteCurrency.in=" + DEFAULT_QUOTE_CURRENCY + "," + UPDATED_QUOTE_CURRENCY);

        // Get all the symbolList where quoteCurrency equals to UPDATED_QUOTE_CURRENCY
        defaultSymbolShouldNotBeFound("quoteCurrency.in=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteCurrency is not null
        defaultSymbolShouldBeFound("quoteCurrency.specified=true");

        // Get all the symbolList where quoteCurrency is null
        defaultSymbolShouldNotBeFound("quoteCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteCurrencyContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteCurrency contains DEFAULT_QUOTE_CURRENCY
        defaultSymbolShouldBeFound("quoteCurrency.contains=" + DEFAULT_QUOTE_CURRENCY);

        // Get all the symbolList where quoteCurrency contains UPDATED_QUOTE_CURRENCY
        defaultSymbolShouldNotBeFound("quoteCurrency.contains=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteCurrency does not contain DEFAULT_QUOTE_CURRENCY
        defaultSymbolShouldNotBeFound("quoteCurrency.doesNotContain=" + DEFAULT_QUOTE_CURRENCY);

        // Get all the symbolList where quoteCurrency does not contain UPDATED_QUOTE_CURRENCY
        defaultSymbolShouldBeFound("quoteCurrency.doesNotContain=" + UPDATED_QUOTE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByFeeCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where feeCurrency equals to DEFAULT_FEE_CURRENCY
        defaultSymbolShouldBeFound("feeCurrency.equals=" + DEFAULT_FEE_CURRENCY);

        // Get all the symbolList where feeCurrency equals to UPDATED_FEE_CURRENCY
        defaultSymbolShouldNotBeFound("feeCurrency.equals=" + UPDATED_FEE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByFeeCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where feeCurrency in DEFAULT_FEE_CURRENCY or UPDATED_FEE_CURRENCY
        defaultSymbolShouldBeFound("feeCurrency.in=" + DEFAULT_FEE_CURRENCY + "," + UPDATED_FEE_CURRENCY);

        // Get all the symbolList where feeCurrency equals to UPDATED_FEE_CURRENCY
        defaultSymbolShouldNotBeFound("feeCurrency.in=" + UPDATED_FEE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByFeeCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where feeCurrency is not null
        defaultSymbolShouldBeFound("feeCurrency.specified=true");

        // Get all the symbolList where feeCurrency is null
        defaultSymbolShouldNotBeFound("feeCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByFeeCurrencyContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where feeCurrency contains DEFAULT_FEE_CURRENCY
        defaultSymbolShouldBeFound("feeCurrency.contains=" + DEFAULT_FEE_CURRENCY);

        // Get all the symbolList where feeCurrency contains UPDATED_FEE_CURRENCY
        defaultSymbolShouldNotBeFound("feeCurrency.contains=" + UPDATED_FEE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByFeeCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where feeCurrency does not contain DEFAULT_FEE_CURRENCY
        defaultSymbolShouldNotBeFound("feeCurrency.doesNotContain=" + DEFAULT_FEE_CURRENCY);

        // Get all the symbolList where feeCurrency does not contain UPDATED_FEE_CURRENCY
        defaultSymbolShouldBeFound("feeCurrency.doesNotContain=" + UPDATED_FEE_CURRENCY);
    }

    @Test
    @Transactional
    void getAllSymbolsByMarketIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where market equals to DEFAULT_MARKET
        defaultSymbolShouldBeFound("market.equals=" + DEFAULT_MARKET);

        // Get all the symbolList where market equals to UPDATED_MARKET
        defaultSymbolShouldNotBeFound("market.equals=" + UPDATED_MARKET);
    }

    @Test
    @Transactional
    void getAllSymbolsByMarketIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where market in DEFAULT_MARKET or UPDATED_MARKET
        defaultSymbolShouldBeFound("market.in=" + DEFAULT_MARKET + "," + UPDATED_MARKET);

        // Get all the symbolList where market equals to UPDATED_MARKET
        defaultSymbolShouldNotBeFound("market.in=" + UPDATED_MARKET);
    }

    @Test
    @Transactional
    void getAllSymbolsByMarketIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where market is not null
        defaultSymbolShouldBeFound("market.specified=true");

        // Get all the symbolList where market is null
        defaultSymbolShouldNotBeFound("market.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByMarketContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where market contains DEFAULT_MARKET
        defaultSymbolShouldBeFound("market.contains=" + DEFAULT_MARKET);

        // Get all the symbolList where market contains UPDATED_MARKET
        defaultSymbolShouldNotBeFound("market.contains=" + UPDATED_MARKET);
    }

    @Test
    @Transactional
    void getAllSymbolsByMarketNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where market does not contain DEFAULT_MARKET
        defaultSymbolShouldNotBeFound("market.doesNotContain=" + DEFAULT_MARKET);

        // Get all the symbolList where market does not contain UPDATED_MARKET
        defaultSymbolShouldBeFound("market.doesNotContain=" + UPDATED_MARKET);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMinSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMinSize equals to DEFAULT_BASE_MIN_SIZE
        defaultSymbolShouldBeFound("baseMinSize.equals=" + DEFAULT_BASE_MIN_SIZE);

        // Get all the symbolList where baseMinSize equals to UPDATED_BASE_MIN_SIZE
        defaultSymbolShouldNotBeFound("baseMinSize.equals=" + UPDATED_BASE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMinSizeIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMinSize in DEFAULT_BASE_MIN_SIZE or UPDATED_BASE_MIN_SIZE
        defaultSymbolShouldBeFound("baseMinSize.in=" + DEFAULT_BASE_MIN_SIZE + "," + UPDATED_BASE_MIN_SIZE);

        // Get all the symbolList where baseMinSize equals to UPDATED_BASE_MIN_SIZE
        defaultSymbolShouldNotBeFound("baseMinSize.in=" + UPDATED_BASE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMinSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMinSize is not null
        defaultSymbolShouldBeFound("baseMinSize.specified=true");

        // Get all the symbolList where baseMinSize is null
        defaultSymbolShouldNotBeFound("baseMinSize.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMinSizeContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMinSize contains DEFAULT_BASE_MIN_SIZE
        defaultSymbolShouldBeFound("baseMinSize.contains=" + DEFAULT_BASE_MIN_SIZE);

        // Get all the symbolList where baseMinSize contains UPDATED_BASE_MIN_SIZE
        defaultSymbolShouldNotBeFound("baseMinSize.contains=" + UPDATED_BASE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMinSizeNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMinSize does not contain DEFAULT_BASE_MIN_SIZE
        defaultSymbolShouldNotBeFound("baseMinSize.doesNotContain=" + DEFAULT_BASE_MIN_SIZE);

        // Get all the symbolList where baseMinSize does not contain UPDATED_BASE_MIN_SIZE
        defaultSymbolShouldBeFound("baseMinSize.doesNotContain=" + UPDATED_BASE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMinSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMinSize equals to DEFAULT_QUOTE_MIN_SIZE
        defaultSymbolShouldBeFound("quoteMinSize.equals=" + DEFAULT_QUOTE_MIN_SIZE);

        // Get all the symbolList where quoteMinSize equals to UPDATED_QUOTE_MIN_SIZE
        defaultSymbolShouldNotBeFound("quoteMinSize.equals=" + UPDATED_QUOTE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMinSizeIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMinSize in DEFAULT_QUOTE_MIN_SIZE or UPDATED_QUOTE_MIN_SIZE
        defaultSymbolShouldBeFound("quoteMinSize.in=" + DEFAULT_QUOTE_MIN_SIZE + "," + UPDATED_QUOTE_MIN_SIZE);

        // Get all the symbolList where quoteMinSize equals to UPDATED_QUOTE_MIN_SIZE
        defaultSymbolShouldNotBeFound("quoteMinSize.in=" + UPDATED_QUOTE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMinSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMinSize is not null
        defaultSymbolShouldBeFound("quoteMinSize.specified=true");

        // Get all the symbolList where quoteMinSize is null
        defaultSymbolShouldNotBeFound("quoteMinSize.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMinSizeContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMinSize contains DEFAULT_QUOTE_MIN_SIZE
        defaultSymbolShouldBeFound("quoteMinSize.contains=" + DEFAULT_QUOTE_MIN_SIZE);

        // Get all the symbolList where quoteMinSize contains UPDATED_QUOTE_MIN_SIZE
        defaultSymbolShouldNotBeFound("quoteMinSize.contains=" + UPDATED_QUOTE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMinSizeNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMinSize does not contain DEFAULT_QUOTE_MIN_SIZE
        defaultSymbolShouldNotBeFound("quoteMinSize.doesNotContain=" + DEFAULT_QUOTE_MIN_SIZE);

        // Get all the symbolList where quoteMinSize does not contain UPDATED_QUOTE_MIN_SIZE
        defaultSymbolShouldBeFound("quoteMinSize.doesNotContain=" + UPDATED_QUOTE_MIN_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMaxSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMaxSize equals to DEFAULT_BASE_MAX_SIZE
        defaultSymbolShouldBeFound("baseMaxSize.equals=" + DEFAULT_BASE_MAX_SIZE);

        // Get all the symbolList where baseMaxSize equals to UPDATED_BASE_MAX_SIZE
        defaultSymbolShouldNotBeFound("baseMaxSize.equals=" + UPDATED_BASE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMaxSizeIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMaxSize in DEFAULT_BASE_MAX_SIZE or UPDATED_BASE_MAX_SIZE
        defaultSymbolShouldBeFound("baseMaxSize.in=" + DEFAULT_BASE_MAX_SIZE + "," + UPDATED_BASE_MAX_SIZE);

        // Get all the symbolList where baseMaxSize equals to UPDATED_BASE_MAX_SIZE
        defaultSymbolShouldNotBeFound("baseMaxSize.in=" + UPDATED_BASE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMaxSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMaxSize is not null
        defaultSymbolShouldBeFound("baseMaxSize.specified=true");

        // Get all the symbolList where baseMaxSize is null
        defaultSymbolShouldNotBeFound("baseMaxSize.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMaxSizeContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMaxSize contains DEFAULT_BASE_MAX_SIZE
        defaultSymbolShouldBeFound("baseMaxSize.contains=" + DEFAULT_BASE_MAX_SIZE);

        // Get all the symbolList where baseMaxSize contains UPDATED_BASE_MAX_SIZE
        defaultSymbolShouldNotBeFound("baseMaxSize.contains=" + UPDATED_BASE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseMaxSizeNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseMaxSize does not contain DEFAULT_BASE_MAX_SIZE
        defaultSymbolShouldNotBeFound("baseMaxSize.doesNotContain=" + DEFAULT_BASE_MAX_SIZE);

        // Get all the symbolList where baseMaxSize does not contain UPDATED_BASE_MAX_SIZE
        defaultSymbolShouldBeFound("baseMaxSize.doesNotContain=" + UPDATED_BASE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMaxSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMaxSize equals to DEFAULT_QUOTE_MAX_SIZE
        defaultSymbolShouldBeFound("quoteMaxSize.equals=" + DEFAULT_QUOTE_MAX_SIZE);

        // Get all the symbolList where quoteMaxSize equals to UPDATED_QUOTE_MAX_SIZE
        defaultSymbolShouldNotBeFound("quoteMaxSize.equals=" + UPDATED_QUOTE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMaxSizeIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMaxSize in DEFAULT_QUOTE_MAX_SIZE or UPDATED_QUOTE_MAX_SIZE
        defaultSymbolShouldBeFound("quoteMaxSize.in=" + DEFAULT_QUOTE_MAX_SIZE + "," + UPDATED_QUOTE_MAX_SIZE);

        // Get all the symbolList where quoteMaxSize equals to UPDATED_QUOTE_MAX_SIZE
        defaultSymbolShouldNotBeFound("quoteMaxSize.in=" + UPDATED_QUOTE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMaxSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMaxSize is not null
        defaultSymbolShouldBeFound("quoteMaxSize.specified=true");

        // Get all the symbolList where quoteMaxSize is null
        defaultSymbolShouldNotBeFound("quoteMaxSize.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMaxSizeContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMaxSize contains DEFAULT_QUOTE_MAX_SIZE
        defaultSymbolShouldBeFound("quoteMaxSize.contains=" + DEFAULT_QUOTE_MAX_SIZE);

        // Get all the symbolList where quoteMaxSize contains UPDATED_QUOTE_MAX_SIZE
        defaultSymbolShouldNotBeFound("quoteMaxSize.contains=" + UPDATED_QUOTE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteMaxSizeNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteMaxSize does not contain DEFAULT_QUOTE_MAX_SIZE
        defaultSymbolShouldNotBeFound("quoteMaxSize.doesNotContain=" + DEFAULT_QUOTE_MAX_SIZE);

        // Get all the symbolList where quoteMaxSize does not contain UPDATED_QUOTE_MAX_SIZE
        defaultSymbolShouldBeFound("quoteMaxSize.doesNotContain=" + UPDATED_QUOTE_MAX_SIZE);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseIncrementIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseIncrement equals to DEFAULT_BASE_INCREMENT
        defaultSymbolShouldBeFound("baseIncrement.equals=" + DEFAULT_BASE_INCREMENT);

        // Get all the symbolList where baseIncrement equals to UPDATED_BASE_INCREMENT
        defaultSymbolShouldNotBeFound("baseIncrement.equals=" + UPDATED_BASE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseIncrementIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseIncrement in DEFAULT_BASE_INCREMENT or UPDATED_BASE_INCREMENT
        defaultSymbolShouldBeFound("baseIncrement.in=" + DEFAULT_BASE_INCREMENT + "," + UPDATED_BASE_INCREMENT);

        // Get all the symbolList where baseIncrement equals to UPDATED_BASE_INCREMENT
        defaultSymbolShouldNotBeFound("baseIncrement.in=" + UPDATED_BASE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseIncrementIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseIncrement is not null
        defaultSymbolShouldBeFound("baseIncrement.specified=true");

        // Get all the symbolList where baseIncrement is null
        defaultSymbolShouldNotBeFound("baseIncrement.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseIncrementContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseIncrement contains DEFAULT_BASE_INCREMENT
        defaultSymbolShouldBeFound("baseIncrement.contains=" + DEFAULT_BASE_INCREMENT);

        // Get all the symbolList where baseIncrement contains UPDATED_BASE_INCREMENT
        defaultSymbolShouldNotBeFound("baseIncrement.contains=" + UPDATED_BASE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByBaseIncrementNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where baseIncrement does not contain DEFAULT_BASE_INCREMENT
        defaultSymbolShouldNotBeFound("baseIncrement.doesNotContain=" + DEFAULT_BASE_INCREMENT);

        // Get all the symbolList where baseIncrement does not contain UPDATED_BASE_INCREMENT
        defaultSymbolShouldBeFound("baseIncrement.doesNotContain=" + UPDATED_BASE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteIncrementIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteIncrement equals to DEFAULT_QUOTE_INCREMENT
        defaultSymbolShouldBeFound("quoteIncrement.equals=" + DEFAULT_QUOTE_INCREMENT);

        // Get all the symbolList where quoteIncrement equals to UPDATED_QUOTE_INCREMENT
        defaultSymbolShouldNotBeFound("quoteIncrement.equals=" + UPDATED_QUOTE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteIncrementIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteIncrement in DEFAULT_QUOTE_INCREMENT or UPDATED_QUOTE_INCREMENT
        defaultSymbolShouldBeFound("quoteIncrement.in=" + DEFAULT_QUOTE_INCREMENT + "," + UPDATED_QUOTE_INCREMENT);

        // Get all the symbolList where quoteIncrement equals to UPDATED_QUOTE_INCREMENT
        defaultSymbolShouldNotBeFound("quoteIncrement.in=" + UPDATED_QUOTE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteIncrementIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteIncrement is not null
        defaultSymbolShouldBeFound("quoteIncrement.specified=true");

        // Get all the symbolList where quoteIncrement is null
        defaultSymbolShouldNotBeFound("quoteIncrement.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteIncrementContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteIncrement contains DEFAULT_QUOTE_INCREMENT
        defaultSymbolShouldBeFound("quoteIncrement.contains=" + DEFAULT_QUOTE_INCREMENT);

        // Get all the symbolList where quoteIncrement contains UPDATED_QUOTE_INCREMENT
        defaultSymbolShouldNotBeFound("quoteIncrement.contains=" + UPDATED_QUOTE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByQuoteIncrementNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where quoteIncrement does not contain DEFAULT_QUOTE_INCREMENT
        defaultSymbolShouldNotBeFound("quoteIncrement.doesNotContain=" + DEFAULT_QUOTE_INCREMENT);

        // Get all the symbolList where quoteIncrement does not contain UPDATED_QUOTE_INCREMENT
        defaultSymbolShouldBeFound("quoteIncrement.doesNotContain=" + UPDATED_QUOTE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceIncrementIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceIncrement equals to DEFAULT_PRICE_INCREMENT
        defaultSymbolShouldBeFound("priceIncrement.equals=" + DEFAULT_PRICE_INCREMENT);

        // Get all the symbolList where priceIncrement equals to UPDATED_PRICE_INCREMENT
        defaultSymbolShouldNotBeFound("priceIncrement.equals=" + UPDATED_PRICE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceIncrementIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceIncrement in DEFAULT_PRICE_INCREMENT or UPDATED_PRICE_INCREMENT
        defaultSymbolShouldBeFound("priceIncrement.in=" + DEFAULT_PRICE_INCREMENT + "," + UPDATED_PRICE_INCREMENT);

        // Get all the symbolList where priceIncrement equals to UPDATED_PRICE_INCREMENT
        defaultSymbolShouldNotBeFound("priceIncrement.in=" + UPDATED_PRICE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceIncrementIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceIncrement is not null
        defaultSymbolShouldBeFound("priceIncrement.specified=true");

        // Get all the symbolList where priceIncrement is null
        defaultSymbolShouldNotBeFound("priceIncrement.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceIncrementContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceIncrement contains DEFAULT_PRICE_INCREMENT
        defaultSymbolShouldBeFound("priceIncrement.contains=" + DEFAULT_PRICE_INCREMENT);

        // Get all the symbolList where priceIncrement contains UPDATED_PRICE_INCREMENT
        defaultSymbolShouldNotBeFound("priceIncrement.contains=" + UPDATED_PRICE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceIncrementNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceIncrement does not contain DEFAULT_PRICE_INCREMENT
        defaultSymbolShouldNotBeFound("priceIncrement.doesNotContain=" + DEFAULT_PRICE_INCREMENT);

        // Get all the symbolList where priceIncrement does not contain UPDATED_PRICE_INCREMENT
        defaultSymbolShouldBeFound("priceIncrement.doesNotContain=" + UPDATED_PRICE_INCREMENT);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceLimitRateIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceLimitRate equals to DEFAULT_PRICE_LIMIT_RATE
        defaultSymbolShouldBeFound("priceLimitRate.equals=" + DEFAULT_PRICE_LIMIT_RATE);

        // Get all the symbolList where priceLimitRate equals to UPDATED_PRICE_LIMIT_RATE
        defaultSymbolShouldNotBeFound("priceLimitRate.equals=" + UPDATED_PRICE_LIMIT_RATE);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceLimitRateIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceLimitRate in DEFAULT_PRICE_LIMIT_RATE or UPDATED_PRICE_LIMIT_RATE
        defaultSymbolShouldBeFound("priceLimitRate.in=" + DEFAULT_PRICE_LIMIT_RATE + "," + UPDATED_PRICE_LIMIT_RATE);

        // Get all the symbolList where priceLimitRate equals to UPDATED_PRICE_LIMIT_RATE
        defaultSymbolShouldNotBeFound("priceLimitRate.in=" + UPDATED_PRICE_LIMIT_RATE);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceLimitRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceLimitRate is not null
        defaultSymbolShouldBeFound("priceLimitRate.specified=true");

        // Get all the symbolList where priceLimitRate is null
        defaultSymbolShouldNotBeFound("priceLimitRate.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceLimitRateContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceLimitRate contains DEFAULT_PRICE_LIMIT_RATE
        defaultSymbolShouldBeFound("priceLimitRate.contains=" + DEFAULT_PRICE_LIMIT_RATE);

        // Get all the symbolList where priceLimitRate contains UPDATED_PRICE_LIMIT_RATE
        defaultSymbolShouldNotBeFound("priceLimitRate.contains=" + UPDATED_PRICE_LIMIT_RATE);
    }

    @Test
    @Transactional
    void getAllSymbolsByPriceLimitRateNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where priceLimitRate does not contain DEFAULT_PRICE_LIMIT_RATE
        defaultSymbolShouldNotBeFound("priceLimitRate.doesNotContain=" + DEFAULT_PRICE_LIMIT_RATE);

        // Get all the symbolList where priceLimitRate does not contain UPDATED_PRICE_LIMIT_RATE
        defaultSymbolShouldBeFound("priceLimitRate.doesNotContain=" + UPDATED_PRICE_LIMIT_RATE);
    }

    @Test
    @Transactional
    void getAllSymbolsByMinFundsIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where minFunds equals to DEFAULT_MIN_FUNDS
        defaultSymbolShouldBeFound("minFunds.equals=" + DEFAULT_MIN_FUNDS);

        // Get all the symbolList where minFunds equals to UPDATED_MIN_FUNDS
        defaultSymbolShouldNotBeFound("minFunds.equals=" + UPDATED_MIN_FUNDS);
    }

    @Test
    @Transactional
    void getAllSymbolsByMinFundsIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where minFunds in DEFAULT_MIN_FUNDS or UPDATED_MIN_FUNDS
        defaultSymbolShouldBeFound("minFunds.in=" + DEFAULT_MIN_FUNDS + "," + UPDATED_MIN_FUNDS);

        // Get all the symbolList where minFunds equals to UPDATED_MIN_FUNDS
        defaultSymbolShouldNotBeFound("minFunds.in=" + UPDATED_MIN_FUNDS);
    }

    @Test
    @Transactional
    void getAllSymbolsByMinFundsIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where minFunds is not null
        defaultSymbolShouldBeFound("minFunds.specified=true");

        // Get all the symbolList where minFunds is null
        defaultSymbolShouldNotBeFound("minFunds.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByMinFundsContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where minFunds contains DEFAULT_MIN_FUNDS
        defaultSymbolShouldBeFound("minFunds.contains=" + DEFAULT_MIN_FUNDS);

        // Get all the symbolList where minFunds contains UPDATED_MIN_FUNDS
        defaultSymbolShouldNotBeFound("minFunds.contains=" + UPDATED_MIN_FUNDS);
    }

    @Test
    @Transactional
    void getAllSymbolsByMinFundsNotContainsSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where minFunds does not contain DEFAULT_MIN_FUNDS
        defaultSymbolShouldNotBeFound("minFunds.doesNotContain=" + DEFAULT_MIN_FUNDS);

        // Get all the symbolList where minFunds does not contain UPDATED_MIN_FUNDS
        defaultSymbolShouldBeFound("minFunds.doesNotContain=" + UPDATED_MIN_FUNDS);
    }

    @Test
    @Transactional
    void getAllSymbolsByIsMarginEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where isMarginEnabled equals to DEFAULT_IS_MARGIN_ENABLED
        defaultSymbolShouldBeFound("isMarginEnabled.equals=" + DEFAULT_IS_MARGIN_ENABLED);

        // Get all the symbolList where isMarginEnabled equals to UPDATED_IS_MARGIN_ENABLED
        defaultSymbolShouldNotBeFound("isMarginEnabled.equals=" + UPDATED_IS_MARGIN_ENABLED);
    }

    @Test
    @Transactional
    void getAllSymbolsByIsMarginEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where isMarginEnabled in DEFAULT_IS_MARGIN_ENABLED or UPDATED_IS_MARGIN_ENABLED
        defaultSymbolShouldBeFound("isMarginEnabled.in=" + DEFAULT_IS_MARGIN_ENABLED + "," + UPDATED_IS_MARGIN_ENABLED);

        // Get all the symbolList where isMarginEnabled equals to UPDATED_IS_MARGIN_ENABLED
        defaultSymbolShouldNotBeFound("isMarginEnabled.in=" + UPDATED_IS_MARGIN_ENABLED);
    }

    @Test
    @Transactional
    void getAllSymbolsByIsMarginEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where isMarginEnabled is not null
        defaultSymbolShouldBeFound("isMarginEnabled.specified=true");

        // Get all the symbolList where isMarginEnabled is null
        defaultSymbolShouldNotBeFound("isMarginEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByEnableTradingIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where enableTrading equals to DEFAULT_ENABLE_TRADING
        defaultSymbolShouldBeFound("enableTrading.equals=" + DEFAULT_ENABLE_TRADING);

        // Get all the symbolList where enableTrading equals to UPDATED_ENABLE_TRADING
        defaultSymbolShouldNotBeFound("enableTrading.equals=" + UPDATED_ENABLE_TRADING);
    }

    @Test
    @Transactional
    void getAllSymbolsByEnableTradingIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where enableTrading in DEFAULT_ENABLE_TRADING or UPDATED_ENABLE_TRADING
        defaultSymbolShouldBeFound("enableTrading.in=" + DEFAULT_ENABLE_TRADING + "," + UPDATED_ENABLE_TRADING);

        // Get all the symbolList where enableTrading equals to UPDATED_ENABLE_TRADING
        defaultSymbolShouldNotBeFound("enableTrading.in=" + UPDATED_ENABLE_TRADING);
    }

    @Test
    @Transactional
    void getAllSymbolsByEnableTradingIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where enableTrading is not null
        defaultSymbolShouldBeFound("enableTrading.specified=true");

        // Get all the symbolList where enableTrading is null
        defaultSymbolShouldNotBeFound("enableTrading.specified=false");
    }

    @Test
    @Transactional
    void getAllSymbolsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where active equals to DEFAULT_ACTIVE
        defaultSymbolShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the symbolList where active equals to UPDATED_ACTIVE
        defaultSymbolShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSymbolsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultSymbolShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the symbolList where active equals to UPDATED_ACTIVE
        defaultSymbolShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSymbolsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        // Get all the symbolList where active is not null
        defaultSymbolShouldBeFound("active.specified=true");

        // Get all the symbolList where active is null
        defaultSymbolShouldNotBeFound("active.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSymbolShouldBeFound(String filter) throws Exception {
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symbol.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].baseCurrency").value(hasItem(DEFAULT_BASE_CURRENCY)))
            .andExpect(jsonPath("$.[*].quoteCurrency").value(hasItem(DEFAULT_QUOTE_CURRENCY)))
            .andExpect(jsonPath("$.[*].feeCurrency").value(hasItem(DEFAULT_FEE_CURRENCY)))
            .andExpect(jsonPath("$.[*].market").value(hasItem(DEFAULT_MARKET)))
            .andExpect(jsonPath("$.[*].baseMinSize").value(hasItem(DEFAULT_BASE_MIN_SIZE)))
            .andExpect(jsonPath("$.[*].quoteMinSize").value(hasItem(DEFAULT_QUOTE_MIN_SIZE)))
            .andExpect(jsonPath("$.[*].baseMaxSize").value(hasItem(DEFAULT_BASE_MAX_SIZE)))
            .andExpect(jsonPath("$.[*].quoteMaxSize").value(hasItem(DEFAULT_QUOTE_MAX_SIZE)))
            .andExpect(jsonPath("$.[*].baseIncrement").value(hasItem(DEFAULT_BASE_INCREMENT)))
            .andExpect(jsonPath("$.[*].quoteIncrement").value(hasItem(DEFAULT_QUOTE_INCREMENT)))
            .andExpect(jsonPath("$.[*].priceIncrement").value(hasItem(DEFAULT_PRICE_INCREMENT)))
            .andExpect(jsonPath("$.[*].priceLimitRate").value(hasItem(DEFAULT_PRICE_LIMIT_RATE)))
            .andExpect(jsonPath("$.[*].minFunds").value(hasItem(DEFAULT_MIN_FUNDS)))
            .andExpect(jsonPath("$.[*].isMarginEnabled").value(hasItem(DEFAULT_IS_MARGIN_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].enableTrading").value(hasItem(DEFAULT_ENABLE_TRADING.booleanValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSymbolShouldNotBeFound(String filter) throws Exception {
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSymbolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSymbol() throws Exception {
        // Get the symbol
        restSymbolMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSymbol() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();

        // Update the symbol
        Symbol updatedSymbol = symbolRepository.findById(symbol.getId()).get();
        // Disconnect from session so that the updates on updatedSymbol are not directly saved in db
        em.detach(updatedSymbol);
        updatedSymbol
            .symbol(UPDATED_SYMBOL)
            .name(UPDATED_NAME)
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .feeCurrency(UPDATED_FEE_CURRENCY)
            .market(UPDATED_MARKET)
            .baseMinSize(UPDATED_BASE_MIN_SIZE)
            .quoteMinSize(UPDATED_QUOTE_MIN_SIZE)
            .baseMaxSize(UPDATED_BASE_MAX_SIZE)
            .quoteMaxSize(UPDATED_QUOTE_MAX_SIZE)
            .baseIncrement(UPDATED_BASE_INCREMENT)
            .quoteIncrement(UPDATED_QUOTE_INCREMENT)
            .priceIncrement(UPDATED_PRICE_INCREMENT)
            .priceLimitRate(UPDATED_PRICE_LIMIT_RATE)
            .minFunds(UPDATED_MIN_FUNDS)
            .isMarginEnabled(UPDATED_IS_MARGIN_ENABLED)
            .enableTrading(UPDATED_ENABLE_TRADING)
            .active(UPDATED_ACTIVE);
        SymbolDTO symbolDTO = symbolMapper.toDto(updatedSymbol);

        restSymbolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symbolDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isOk());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
        Symbol testSymbol = symbolList.get(symbolList.size() - 1);
        assertThat(testSymbol.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testSymbol.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSymbol.getBaseCurrency()).isEqualTo(UPDATED_BASE_CURRENCY);
        assertThat(testSymbol.getQuoteCurrency()).isEqualTo(UPDATED_QUOTE_CURRENCY);
        assertThat(testSymbol.getFeeCurrency()).isEqualTo(UPDATED_FEE_CURRENCY);
        assertThat(testSymbol.getMarket()).isEqualTo(UPDATED_MARKET);
        assertThat(testSymbol.getBaseMinSize()).isEqualTo(UPDATED_BASE_MIN_SIZE);
        assertThat(testSymbol.getQuoteMinSize()).isEqualTo(UPDATED_QUOTE_MIN_SIZE);
        assertThat(testSymbol.getBaseMaxSize()).isEqualTo(UPDATED_BASE_MAX_SIZE);
        assertThat(testSymbol.getQuoteMaxSize()).isEqualTo(UPDATED_QUOTE_MAX_SIZE);
        assertThat(testSymbol.getBaseIncrement()).isEqualTo(UPDATED_BASE_INCREMENT);
        assertThat(testSymbol.getQuoteIncrement()).isEqualTo(UPDATED_QUOTE_INCREMENT);
        assertThat(testSymbol.getPriceIncrement()).isEqualTo(UPDATED_PRICE_INCREMENT);
        assertThat(testSymbol.getPriceLimitRate()).isEqualTo(UPDATED_PRICE_LIMIT_RATE);
        assertThat(testSymbol.getMinFunds()).isEqualTo(UPDATED_MIN_FUNDS);
        assertThat(testSymbol.getIsMarginEnabled()).isEqualTo(UPDATED_IS_MARGIN_ENABLED);
        assertThat(testSymbol.getEnableTrading()).isEqualTo(UPDATED_ENABLE_TRADING);
        assertThat(testSymbol.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symbolDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(symbolDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSymbolWithPatch() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();

        // Update the symbol using partial update
        Symbol partialUpdatedSymbol = new Symbol();
        partialUpdatedSymbol.setId(symbol.getId());

        partialUpdatedSymbol
            .symbol(UPDATED_SYMBOL)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .market(UPDATED_MARKET)
            .baseMaxSize(UPDATED_BASE_MAX_SIZE)
            .quoteMaxSize(UPDATED_QUOTE_MAX_SIZE)
            .baseIncrement(UPDATED_BASE_INCREMENT)
            .priceIncrement(UPDATED_PRICE_INCREMENT)
            .priceLimitRate(UPDATED_PRICE_LIMIT_RATE)
            .minFunds(UPDATED_MIN_FUNDS)
            .enableTrading(UPDATED_ENABLE_TRADING)
            .active(UPDATED_ACTIVE);

        restSymbolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymbol.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSymbol))
            )
            .andExpect(status().isOk());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
        Symbol testSymbol = symbolList.get(symbolList.size() - 1);
        assertThat(testSymbol.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testSymbol.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSymbol.getBaseCurrency()).isEqualTo(DEFAULT_BASE_CURRENCY);
        assertThat(testSymbol.getQuoteCurrency()).isEqualTo(UPDATED_QUOTE_CURRENCY);
        assertThat(testSymbol.getFeeCurrency()).isEqualTo(DEFAULT_FEE_CURRENCY);
        assertThat(testSymbol.getMarket()).isEqualTo(UPDATED_MARKET);
        assertThat(testSymbol.getBaseMinSize()).isEqualTo(DEFAULT_BASE_MIN_SIZE);
        assertThat(testSymbol.getQuoteMinSize()).isEqualTo(DEFAULT_QUOTE_MIN_SIZE);
        assertThat(testSymbol.getBaseMaxSize()).isEqualTo(UPDATED_BASE_MAX_SIZE);
        assertThat(testSymbol.getQuoteMaxSize()).isEqualTo(UPDATED_QUOTE_MAX_SIZE);
        assertThat(testSymbol.getBaseIncrement()).isEqualTo(UPDATED_BASE_INCREMENT);
        assertThat(testSymbol.getQuoteIncrement()).isEqualTo(DEFAULT_QUOTE_INCREMENT);
        assertThat(testSymbol.getPriceIncrement()).isEqualTo(UPDATED_PRICE_INCREMENT);
        assertThat(testSymbol.getPriceLimitRate()).isEqualTo(UPDATED_PRICE_LIMIT_RATE);
        assertThat(testSymbol.getMinFunds()).isEqualTo(UPDATED_MIN_FUNDS);
        assertThat(testSymbol.getIsMarginEnabled()).isEqualTo(DEFAULT_IS_MARGIN_ENABLED);
        assertThat(testSymbol.getEnableTrading()).isEqualTo(UPDATED_ENABLE_TRADING);
        assertThat(testSymbol.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateSymbolWithPatch() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();

        // Update the symbol using partial update
        Symbol partialUpdatedSymbol = new Symbol();
        partialUpdatedSymbol.setId(symbol.getId());

        partialUpdatedSymbol
            .symbol(UPDATED_SYMBOL)
            .name(UPDATED_NAME)
            .baseCurrency(UPDATED_BASE_CURRENCY)
            .quoteCurrency(UPDATED_QUOTE_CURRENCY)
            .feeCurrency(UPDATED_FEE_CURRENCY)
            .market(UPDATED_MARKET)
            .baseMinSize(UPDATED_BASE_MIN_SIZE)
            .quoteMinSize(UPDATED_QUOTE_MIN_SIZE)
            .baseMaxSize(UPDATED_BASE_MAX_SIZE)
            .quoteMaxSize(UPDATED_QUOTE_MAX_SIZE)
            .baseIncrement(UPDATED_BASE_INCREMENT)
            .quoteIncrement(UPDATED_QUOTE_INCREMENT)
            .priceIncrement(UPDATED_PRICE_INCREMENT)
            .priceLimitRate(UPDATED_PRICE_LIMIT_RATE)
            .minFunds(UPDATED_MIN_FUNDS)
            .isMarginEnabled(UPDATED_IS_MARGIN_ENABLED)
            .enableTrading(UPDATED_ENABLE_TRADING)
            .active(UPDATED_ACTIVE);

        restSymbolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymbol.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSymbol))
            )
            .andExpect(status().isOk());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
        Symbol testSymbol = symbolList.get(symbolList.size() - 1);
        assertThat(testSymbol.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testSymbol.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSymbol.getBaseCurrency()).isEqualTo(UPDATED_BASE_CURRENCY);
        assertThat(testSymbol.getQuoteCurrency()).isEqualTo(UPDATED_QUOTE_CURRENCY);
        assertThat(testSymbol.getFeeCurrency()).isEqualTo(UPDATED_FEE_CURRENCY);
        assertThat(testSymbol.getMarket()).isEqualTo(UPDATED_MARKET);
        assertThat(testSymbol.getBaseMinSize()).isEqualTo(UPDATED_BASE_MIN_SIZE);
        assertThat(testSymbol.getQuoteMinSize()).isEqualTo(UPDATED_QUOTE_MIN_SIZE);
        assertThat(testSymbol.getBaseMaxSize()).isEqualTo(UPDATED_BASE_MAX_SIZE);
        assertThat(testSymbol.getQuoteMaxSize()).isEqualTo(UPDATED_QUOTE_MAX_SIZE);
        assertThat(testSymbol.getBaseIncrement()).isEqualTo(UPDATED_BASE_INCREMENT);
        assertThat(testSymbol.getQuoteIncrement()).isEqualTo(UPDATED_QUOTE_INCREMENT);
        assertThat(testSymbol.getPriceIncrement()).isEqualTo(UPDATED_PRICE_INCREMENT);
        assertThat(testSymbol.getPriceLimitRate()).isEqualTo(UPDATED_PRICE_LIMIT_RATE);
        assertThat(testSymbol.getMinFunds()).isEqualTo(UPDATED_MIN_FUNDS);
        assertThat(testSymbol.getIsMarginEnabled()).isEqualTo(UPDATED_IS_MARGIN_ENABLED);
        assertThat(testSymbol.getEnableTrading()).isEqualTo(UPDATED_ENABLE_TRADING);
        assertThat(testSymbol.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, symbolDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSymbol() throws Exception {
        int databaseSizeBeforeUpdate = symbolRepository.findAll().size();
        symbol.setId(count.incrementAndGet());

        // Create the Symbol
        SymbolDTO symbolDTO = symbolMapper.toDto(symbol);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymbolMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(symbolDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Symbol in the database
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSymbol() throws Exception {
        // Initialize the database
        symbolRepository.saveAndFlush(symbol);

        int databaseSizeBeforeDelete = symbolRepository.findAll().size();

        // Delete the symbol
        restSymbolMockMvc
            .perform(delete(ENTITY_API_URL_ID, symbol.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Symbol> symbolList = symbolRepository.findAll();
        assertThat(symbolList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
