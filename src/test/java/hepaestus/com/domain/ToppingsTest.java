package hepaestus.com.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import hepaestus.com.web.rest.TestUtil;

public class ToppingsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Toppings.class);
        Toppings toppings1 = new Toppings();
        toppings1.setId(1L);
        Toppings toppings2 = new Toppings();
        toppings2.setId(toppings1.getId());
        assertThat(toppings1).isEqualTo(toppings2);
        toppings2.setId(2L);
        assertThat(toppings1).isNotEqualTo(toppings2);
        toppings1.setId(null);
        assertThat(toppings1).isNotEqualTo(toppings2);
    }
}
