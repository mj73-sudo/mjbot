package org.mjbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mjbot.web.rest.TestUtil;

class SymbolDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SymbolDTO.class);
        SymbolDTO symbolDTO1 = new SymbolDTO();
        symbolDTO1.setId(1L);
        SymbolDTO symbolDTO2 = new SymbolDTO();
        assertThat(symbolDTO1).isNotEqualTo(symbolDTO2);
        symbolDTO2.setId(symbolDTO1.getId());
        assertThat(symbolDTO1).isEqualTo(symbolDTO2);
        symbolDTO2.setId(2L);
        assertThat(symbolDTO1).isNotEqualTo(symbolDTO2);
        symbolDTO1.setId(null);
        assertThat(symbolDTO1).isNotEqualTo(symbolDTO2);
    }
}
