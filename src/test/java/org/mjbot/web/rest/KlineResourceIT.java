package org.mjbot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mjbot.IntegrationTest;
import org.mjbot.domain.Kline;
import org.mjbot.domain.Symbol;
import org.mjbot.repository.KlineRepository;
import org.mjbot.service.KlineService;
import org.mjbot.service.criteria.KlineCriteria;
import org.mjbot.service.dto.KlineDTO;
import org.mjbot.service.mapper.KlineMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link KlineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KlineResourceIT {

    private static final Long DEFAULT_TIME = 1L;
    private static final Long UPDATED_TIME = 2L;
    private static final Long SMALLER_TIME = 1L - 1L;

    private static final String DEFAULT_OPEN = "AAAAAAAAAA";
    private static final String UPDATED_OPEN = "BBBBBBBBBB";

    private static final String DEFAULT_CLOSE = "AAAAAAAAAA";
    private static final String UPDATED_CLOSE = "BBBBBBBBBB";

    private static final String DEFAULT_HIGH = "AAAAAAAAAA";
    private static final String UPDATED_HIGH = "BBBBBBBBBB";

    private static final String DEFAULT_LOW = "AAAAAAAAAA";
    private static final String UPDATED_LOW = "BBBBBBBBBB";

    private static final String DEFAULT_VOLUME = "AAAAAAAAAA";
    private static final String UPDATED_VOLUME = "BBBBBBBBBB";

    private static final String DEFAULT_TURNOVER = "AAAAAAAAAA";
    private static final String UPDATED_TURNOVER = "BBBBBBBBBB";

    private static final String DEFAULT_TIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TIME_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/klines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private KlineRepository klineRepository;

    @Mock
    private KlineRepository klineRepositoryMock;

    @Autowired
    private KlineMapper klineMapper;

    @Mock
    private KlineService klineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKlineMockMvc;

    private Kline kline;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kline createEntity(EntityManager em) {
        Kline kline = new Kline()
            .time(DEFAULT_TIME)
            .open(DEFAULT_OPEN)
            .close(DEFAULT_CLOSE)
            .high(DEFAULT_HIGH)
            .low(DEFAULT_LOW)
            .volume(DEFAULT_VOLUME)
            .turnover(DEFAULT_TURNOVER)
            .timeType(DEFAULT_TIME_TYPE);
        return kline;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kline createUpdatedEntity(EntityManager em) {
        Kline kline = new Kline()
            .time(UPDATED_TIME)
            .open(UPDATED_OPEN)
            .close(UPDATED_CLOSE)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .volume(UPDATED_VOLUME)
            .turnover(UPDATED_TURNOVER)
            .timeType(UPDATED_TIME_TYPE);
        return kline;
    }

    @BeforeEach
    public void initTest() {
        kline = createEntity(em);
    }

    @Test
    @Transactional
    void createKline() throws Exception {
        int databaseSizeBeforeCreate = klineRepository.findAll().size();
        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);
        restKlineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(klineDTO)))
            .andExpect(status().isCreated());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeCreate + 1);
        Kline testKline = klineList.get(klineList.size() - 1);
        assertThat(testKline.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testKline.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testKline.getClose()).isEqualTo(DEFAULT_CLOSE);
        assertThat(testKline.getHigh()).isEqualTo(DEFAULT_HIGH);
        assertThat(testKline.getLow()).isEqualTo(DEFAULT_LOW);
        assertThat(testKline.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testKline.getTurnover()).isEqualTo(DEFAULT_TURNOVER);
        assertThat(testKline.getTimeType()).isEqualTo(DEFAULT_TIME_TYPE);
    }

    @Test
    @Transactional
    void createKlineWithExistingId() throws Exception {
        // Create the Kline with an existing ID
        kline.setId(1L);
        KlineDTO klineDTO = klineMapper.toDto(kline);

        int databaseSizeBeforeCreate = klineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKlineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(klineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllKlines() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList
        restKlineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kline.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.intValue())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN)))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE)))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH)))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
            .andExpect(jsonPath("$.[*].turnover").value(hasItem(DEFAULT_TURNOVER)))
            .andExpect(jsonPath("$.[*].timeType").value(hasItem(DEFAULT_TIME_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKlinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(klineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKlineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(klineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKlinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(klineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKlineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(klineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKline() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get the kline
        restKlineMockMvc
            .perform(get(ENTITY_API_URL_ID, kline.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(kline.getId().intValue()))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.intValue()))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN))
            .andExpect(jsonPath("$.close").value(DEFAULT_CLOSE))
            .andExpect(jsonPath("$.high").value(DEFAULT_HIGH))
            .andExpect(jsonPath("$.low").value(DEFAULT_LOW))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME))
            .andExpect(jsonPath("$.turnover").value(DEFAULT_TURNOVER))
            .andExpect(jsonPath("$.timeType").value(DEFAULT_TIME_TYPE));
    }

    @Test
    @Transactional
    void getKlinesByIdFiltering() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        Long id = kline.getId();

        defaultKlineShouldBeFound("id.equals=" + id);
        defaultKlineShouldNotBeFound("id.notEquals=" + id);

        defaultKlineShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultKlineShouldNotBeFound("id.greaterThan=" + id);

        defaultKlineShouldBeFound("id.lessThanOrEqual=" + id);
        defaultKlineShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time equals to DEFAULT_TIME
        defaultKlineShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the klineList where time equals to UPDATED_TIME
        defaultKlineShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time in DEFAULT_TIME or UPDATED_TIME
        defaultKlineShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the klineList where time equals to UPDATED_TIME
        defaultKlineShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time is not null
        defaultKlineShouldBeFound("time.specified=true");

        // Get all the klineList where time is null
        defaultKlineShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time is greater than or equal to DEFAULT_TIME
        defaultKlineShouldBeFound("time.greaterThanOrEqual=" + DEFAULT_TIME);

        // Get all the klineList where time is greater than or equal to UPDATED_TIME
        defaultKlineShouldNotBeFound("time.greaterThanOrEqual=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time is less than or equal to DEFAULT_TIME
        defaultKlineShouldBeFound("time.lessThanOrEqual=" + DEFAULT_TIME);

        // Get all the klineList where time is less than or equal to SMALLER_TIME
        defaultKlineShouldNotBeFound("time.lessThanOrEqual=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time is less than DEFAULT_TIME
        defaultKlineShouldNotBeFound("time.lessThan=" + DEFAULT_TIME);

        // Get all the klineList where time is less than UPDATED_TIME
        defaultKlineShouldBeFound("time.lessThan=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where time is greater than DEFAULT_TIME
        defaultKlineShouldNotBeFound("time.greaterThan=" + DEFAULT_TIME);

        // Get all the klineList where time is greater than SMALLER_TIME
        defaultKlineShouldBeFound("time.greaterThan=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    void getAllKlinesByOpenIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where open equals to DEFAULT_OPEN
        defaultKlineShouldBeFound("open.equals=" + DEFAULT_OPEN);

        // Get all the klineList where open equals to UPDATED_OPEN
        defaultKlineShouldNotBeFound("open.equals=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllKlinesByOpenIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where open in DEFAULT_OPEN or UPDATED_OPEN
        defaultKlineShouldBeFound("open.in=" + DEFAULT_OPEN + "," + UPDATED_OPEN);

        // Get all the klineList where open equals to UPDATED_OPEN
        defaultKlineShouldNotBeFound("open.in=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllKlinesByOpenIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where open is not null
        defaultKlineShouldBeFound("open.specified=true");

        // Get all the klineList where open is null
        defaultKlineShouldNotBeFound("open.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByOpenContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where open contains DEFAULT_OPEN
        defaultKlineShouldBeFound("open.contains=" + DEFAULT_OPEN);

        // Get all the klineList where open contains UPDATED_OPEN
        defaultKlineShouldNotBeFound("open.contains=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllKlinesByOpenNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where open does not contain DEFAULT_OPEN
        defaultKlineShouldNotBeFound("open.doesNotContain=" + DEFAULT_OPEN);

        // Get all the klineList where open does not contain UPDATED_OPEN
        defaultKlineShouldBeFound("open.doesNotContain=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    void getAllKlinesByCloseIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where close equals to DEFAULT_CLOSE
        defaultKlineShouldBeFound("close.equals=" + DEFAULT_CLOSE);

        // Get all the klineList where close equals to UPDATED_CLOSE
        defaultKlineShouldNotBeFound("close.equals=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    void getAllKlinesByCloseIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where close in DEFAULT_CLOSE or UPDATED_CLOSE
        defaultKlineShouldBeFound("close.in=" + DEFAULT_CLOSE + "," + UPDATED_CLOSE);

        // Get all the klineList where close equals to UPDATED_CLOSE
        defaultKlineShouldNotBeFound("close.in=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    void getAllKlinesByCloseIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where close is not null
        defaultKlineShouldBeFound("close.specified=true");

        // Get all the klineList where close is null
        defaultKlineShouldNotBeFound("close.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByCloseContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where close contains DEFAULT_CLOSE
        defaultKlineShouldBeFound("close.contains=" + DEFAULT_CLOSE);

        // Get all the klineList where close contains UPDATED_CLOSE
        defaultKlineShouldNotBeFound("close.contains=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    void getAllKlinesByCloseNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where close does not contain DEFAULT_CLOSE
        defaultKlineShouldNotBeFound("close.doesNotContain=" + DEFAULT_CLOSE);

        // Get all the klineList where close does not contain UPDATED_CLOSE
        defaultKlineShouldBeFound("close.doesNotContain=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    void getAllKlinesByHighIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where high equals to DEFAULT_HIGH
        defaultKlineShouldBeFound("high.equals=" + DEFAULT_HIGH);

        // Get all the klineList where high equals to UPDATED_HIGH
        defaultKlineShouldNotBeFound("high.equals=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    void getAllKlinesByHighIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where high in DEFAULT_HIGH or UPDATED_HIGH
        defaultKlineShouldBeFound("high.in=" + DEFAULT_HIGH + "," + UPDATED_HIGH);

        // Get all the klineList where high equals to UPDATED_HIGH
        defaultKlineShouldNotBeFound("high.in=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    void getAllKlinesByHighIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where high is not null
        defaultKlineShouldBeFound("high.specified=true");

        // Get all the klineList where high is null
        defaultKlineShouldNotBeFound("high.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByHighContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where high contains DEFAULT_HIGH
        defaultKlineShouldBeFound("high.contains=" + DEFAULT_HIGH);

        // Get all the klineList where high contains UPDATED_HIGH
        defaultKlineShouldNotBeFound("high.contains=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    void getAllKlinesByHighNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where high does not contain DEFAULT_HIGH
        defaultKlineShouldNotBeFound("high.doesNotContain=" + DEFAULT_HIGH);

        // Get all the klineList where high does not contain UPDATED_HIGH
        defaultKlineShouldBeFound("high.doesNotContain=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    void getAllKlinesByLowIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where low equals to DEFAULT_LOW
        defaultKlineShouldBeFound("low.equals=" + DEFAULT_LOW);

        // Get all the klineList where low equals to UPDATED_LOW
        defaultKlineShouldNotBeFound("low.equals=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    void getAllKlinesByLowIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where low in DEFAULT_LOW or UPDATED_LOW
        defaultKlineShouldBeFound("low.in=" + DEFAULT_LOW + "," + UPDATED_LOW);

        // Get all the klineList where low equals to UPDATED_LOW
        defaultKlineShouldNotBeFound("low.in=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    void getAllKlinesByLowIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where low is not null
        defaultKlineShouldBeFound("low.specified=true");

        // Get all the klineList where low is null
        defaultKlineShouldNotBeFound("low.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByLowContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where low contains DEFAULT_LOW
        defaultKlineShouldBeFound("low.contains=" + DEFAULT_LOW);

        // Get all the klineList where low contains UPDATED_LOW
        defaultKlineShouldNotBeFound("low.contains=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    void getAllKlinesByLowNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where low does not contain DEFAULT_LOW
        defaultKlineShouldNotBeFound("low.doesNotContain=" + DEFAULT_LOW);

        // Get all the klineList where low does not contain UPDATED_LOW
        defaultKlineShouldBeFound("low.doesNotContain=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    void getAllKlinesByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where volume equals to DEFAULT_VOLUME
        defaultKlineShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the klineList where volume equals to UPDATED_VOLUME
        defaultKlineShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllKlinesByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultKlineShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the klineList where volume equals to UPDATED_VOLUME
        defaultKlineShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllKlinesByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where volume is not null
        defaultKlineShouldBeFound("volume.specified=true");

        // Get all the klineList where volume is null
        defaultKlineShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByVolumeContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where volume contains DEFAULT_VOLUME
        defaultKlineShouldBeFound("volume.contains=" + DEFAULT_VOLUME);

        // Get all the klineList where volume contains UPDATED_VOLUME
        defaultKlineShouldNotBeFound("volume.contains=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllKlinesByVolumeNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where volume does not contain DEFAULT_VOLUME
        defaultKlineShouldNotBeFound("volume.doesNotContain=" + DEFAULT_VOLUME);

        // Get all the klineList where volume does not contain UPDATED_VOLUME
        defaultKlineShouldBeFound("volume.doesNotContain=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllKlinesByTurnoverIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where turnover equals to DEFAULT_TURNOVER
        defaultKlineShouldBeFound("turnover.equals=" + DEFAULT_TURNOVER);

        // Get all the klineList where turnover equals to UPDATED_TURNOVER
        defaultKlineShouldNotBeFound("turnover.equals=" + UPDATED_TURNOVER);
    }

    @Test
    @Transactional
    void getAllKlinesByTurnoverIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where turnover in DEFAULT_TURNOVER or UPDATED_TURNOVER
        defaultKlineShouldBeFound("turnover.in=" + DEFAULT_TURNOVER + "," + UPDATED_TURNOVER);

        // Get all the klineList where turnover equals to UPDATED_TURNOVER
        defaultKlineShouldNotBeFound("turnover.in=" + UPDATED_TURNOVER);
    }

    @Test
    @Transactional
    void getAllKlinesByTurnoverIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where turnover is not null
        defaultKlineShouldBeFound("turnover.specified=true");

        // Get all the klineList where turnover is null
        defaultKlineShouldNotBeFound("turnover.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByTurnoverContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where turnover contains DEFAULT_TURNOVER
        defaultKlineShouldBeFound("turnover.contains=" + DEFAULT_TURNOVER);

        // Get all the klineList where turnover contains UPDATED_TURNOVER
        defaultKlineShouldNotBeFound("turnover.contains=" + UPDATED_TURNOVER);
    }

    @Test
    @Transactional
    void getAllKlinesByTurnoverNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where turnover does not contain DEFAULT_TURNOVER
        defaultKlineShouldNotBeFound("turnover.doesNotContain=" + DEFAULT_TURNOVER);

        // Get all the klineList where turnover does not contain UPDATED_TURNOVER
        defaultKlineShouldBeFound("turnover.doesNotContain=" + UPDATED_TURNOVER);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where timeType equals to DEFAULT_TIME_TYPE
        defaultKlineShouldBeFound("timeType.equals=" + DEFAULT_TIME_TYPE);

        // Get all the klineList where timeType equals to UPDATED_TIME_TYPE
        defaultKlineShouldNotBeFound("timeType.equals=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where timeType in DEFAULT_TIME_TYPE or UPDATED_TIME_TYPE
        defaultKlineShouldBeFound("timeType.in=" + DEFAULT_TIME_TYPE + "," + UPDATED_TIME_TYPE);

        // Get all the klineList where timeType equals to UPDATED_TIME_TYPE
        defaultKlineShouldNotBeFound("timeType.in=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where timeType is not null
        defaultKlineShouldBeFound("timeType.specified=true");

        // Get all the klineList where timeType is null
        defaultKlineShouldNotBeFound("timeType.specified=false");
    }

    @Test
    @Transactional
    void getAllKlinesByTimeTypeContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where timeType contains DEFAULT_TIME_TYPE
        defaultKlineShouldBeFound("timeType.contains=" + DEFAULT_TIME_TYPE);

        // Get all the klineList where timeType contains UPDATED_TIME_TYPE
        defaultKlineShouldNotBeFound("timeType.contains=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllKlinesByTimeTypeNotContainsSomething() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        // Get all the klineList where timeType does not contain DEFAULT_TIME_TYPE
        defaultKlineShouldNotBeFound("timeType.doesNotContain=" + DEFAULT_TIME_TYPE);

        // Get all the klineList where timeType does not contain UPDATED_TIME_TYPE
        defaultKlineShouldBeFound("timeType.doesNotContain=" + UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void getAllKlinesBySymbolIsEqualToSomething() throws Exception {
        Symbol symbol;
        if (TestUtil.findAll(em, Symbol.class).isEmpty()) {
            klineRepository.saveAndFlush(kline);
            symbol = SymbolResourceIT.createEntity(em);
        } else {
            symbol = TestUtil.findAll(em, Symbol.class).get(0);
        }
        em.persist(symbol);
        em.flush();
        kline.setSymbol(symbol);
        klineRepository.saveAndFlush(kline);
        Long symbolId = symbol.getId();

        // Get all the klineList where symbol equals to symbolId
        defaultKlineShouldBeFound("symbolId.equals=" + symbolId);

        // Get all the klineList where symbol equals to (symbolId + 1)
        defaultKlineShouldNotBeFound("symbolId.equals=" + (symbolId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKlineShouldBeFound(String filter) throws Exception {
        restKlineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kline.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.intValue())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN)))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE)))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH)))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)))
            .andExpect(jsonPath("$.[*].turnover").value(hasItem(DEFAULT_TURNOVER)))
            .andExpect(jsonPath("$.[*].timeType").value(hasItem(DEFAULT_TIME_TYPE)));

        // Check, that the count call also returns 1
        restKlineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKlineShouldNotBeFound(String filter) throws Exception {
        restKlineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKlineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKline() throws Exception {
        // Get the kline
        restKlineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKline() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        int databaseSizeBeforeUpdate = klineRepository.findAll().size();

        // Update the kline
        Kline updatedKline = klineRepository.findById(kline.getId()).get();
        // Disconnect from session so that the updates on updatedKline are not directly saved in db
        em.detach(updatedKline);
        updatedKline
            .time(UPDATED_TIME)
            .open(UPDATED_OPEN)
            .close(UPDATED_CLOSE)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .volume(UPDATED_VOLUME)
            .turnover(UPDATED_TURNOVER)
            .timeType(UPDATED_TIME_TYPE);
        KlineDTO klineDTO = klineMapper.toDto(updatedKline);

        restKlineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, klineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(klineDTO))
            )
            .andExpect(status().isOk());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
        Kline testKline = klineList.get(klineList.size() - 1);
        assertThat(testKline.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testKline.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testKline.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testKline.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testKline.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testKline.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testKline.getTurnover()).isEqualTo(UPDATED_TURNOVER);
        assertThat(testKline.getTimeType()).isEqualTo(UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, klineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(klineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(klineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(klineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKlineWithPatch() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        int databaseSizeBeforeUpdate = klineRepository.findAll().size();

        // Update the kline using partial update
        Kline partialUpdatedKline = new Kline();
        partialUpdatedKline.setId(kline.getId());

        partialUpdatedKline.open(UPDATED_OPEN).close(UPDATED_CLOSE).high(UPDATED_HIGH).low(UPDATED_LOW).volume(UPDATED_VOLUME);

        restKlineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKline.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedKline))
            )
            .andExpect(status().isOk());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
        Kline testKline = klineList.get(klineList.size() - 1);
        assertThat(testKline.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testKline.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testKline.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testKline.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testKline.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testKline.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testKline.getTurnover()).isEqualTo(DEFAULT_TURNOVER);
        assertThat(testKline.getTimeType()).isEqualTo(DEFAULT_TIME_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateKlineWithPatch() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        int databaseSizeBeforeUpdate = klineRepository.findAll().size();

        // Update the kline using partial update
        Kline partialUpdatedKline = new Kline();
        partialUpdatedKline.setId(kline.getId());

        partialUpdatedKline
            .time(UPDATED_TIME)
            .open(UPDATED_OPEN)
            .close(UPDATED_CLOSE)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .volume(UPDATED_VOLUME)
            .turnover(UPDATED_TURNOVER)
            .timeType(UPDATED_TIME_TYPE);

        restKlineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKline.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedKline))
            )
            .andExpect(status().isOk());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
        Kline testKline = klineList.get(klineList.size() - 1);
        assertThat(testKline.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testKline.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testKline.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testKline.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testKline.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testKline.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testKline.getTurnover()).isEqualTo(UPDATED_TURNOVER);
        assertThat(testKline.getTimeType()).isEqualTo(UPDATED_TIME_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, klineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(klineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(klineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKline() throws Exception {
        int databaseSizeBeforeUpdate = klineRepository.findAll().size();
        kline.setId(count.incrementAndGet());

        // Create the Kline
        KlineDTO klineDTO = klineMapper.toDto(kline);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKlineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(klineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kline in the database
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKline() throws Exception {
        // Initialize the database
        klineRepository.saveAndFlush(kline);

        int databaseSizeBeforeDelete = klineRepository.findAll().size();

        // Delete the kline
        restKlineMockMvc
            .perform(delete(ENTITY_API_URL_ID, kline.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Kline> klineList = klineRepository.findAll();
        assertThat(klineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
