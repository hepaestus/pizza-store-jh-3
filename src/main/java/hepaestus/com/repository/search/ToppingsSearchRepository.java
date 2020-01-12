package hepaestus.com.repository.search;

import hepaestus.com.domain.Toppings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Toppings} entity.
 */
public interface ToppingsSearchRepository extends ElasticsearchRepository<Toppings, Long> {
}
