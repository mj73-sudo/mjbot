package org.mjbot.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mjbot.web.rest.TestUtil;

class SymbolTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Symbol.class);
        Symbol symbol1 = new Symbol();
        symbol1.setId(1L);
        Symbol symbol2 = new Symbol();
        symbol2.setId(symbol1.getId());
        assertThat(symbol1).isEqualTo(symbol2);
        symbol2.setId(2L);
        assertThat(symbol1).isNotEqualTo(symbol2);
        symbol1.setId(null);
        assertThat(symbol1).isNotEqualTo(symbol2);
    }
}
