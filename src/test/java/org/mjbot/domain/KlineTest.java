package org.mjbot.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mjbot.web.rest.TestUtil;

class KlineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Kline.class);
        Kline kline1 = new Kline();
        kline1.setId(1L);
        Kline kline2 = new Kline();
        kline2.setId(kline1.getId());
        assertThat(kline1).isEqualTo(kline2);
        kline2.setId(2L);
        assertThat(kline1).isNotEqualTo(kline2);
        kline1.setId(null);
        assertThat(kline1).isNotEqualTo(kline2);
    }
}
