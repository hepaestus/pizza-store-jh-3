package hepaestus.com.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PizzaSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PizzaSearchRepositoryMockConfiguration {

    @MockBean
    private PizzaSearchRepository mockPizzaSearchRepository;

}
