package org.mjbot.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SymbolMapperTest {

    private SymbolMapper symbolMapper;

    @BeforeEach
    public void setUp() {
        symbolMapper = new SymbolMapperImpl();
    }
}
