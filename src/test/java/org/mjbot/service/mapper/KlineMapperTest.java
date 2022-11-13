package org.mjbot.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KlineMapperTest {

    private KlineMapper klineMapper;

    @BeforeEach
    public void setUp() {
        klineMapper = new KlineMapperImpl();
    }
}
