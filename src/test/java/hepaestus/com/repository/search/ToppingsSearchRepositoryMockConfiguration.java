package hepaestus.com.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ToppingsSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ToppingsSearchRepositoryMockConfiguration {

    @MockBean
    private ToppingsSearchRepository mockToppingsSearchRepository;

}
