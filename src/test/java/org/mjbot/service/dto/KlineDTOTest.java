package org.mjbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mjbot.web.rest.TestUtil;

class KlineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KlineDTO.class);
        KlineDTO klineDTO1 = new KlineDTO();
        klineDTO1.setId(1L);
        KlineDTO klineDTO2 = new KlineDTO();
        assertThat(klineDTO1).isNotEqualTo(klineDTO2);
        klineDTO2.setId(klineDTO1.getId());
        assertThat(klineDTO1).isEqualTo(klineDTO2);
        klineDTO2.setId(2L);
        assertThat(klineDTO1).isNotEqualTo(klineDTO2);
        klineDTO1.setId(null);
        assertThat(klineDTO1).isNotEqualTo(klineDTO2);
    }
}
