package hepaestus.com.repository.search;

import hepaestus.com.domain.Pizza;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Pizza} entity.
 */
public interface PizzaSearchRepository extends ElasticsearchRepository<Pizza, Long> {
}
