package hepaestus.com.web.rest;

import hepaestus.com.domain.Pizza;
import hepaestus.com.repository.PizzaRepository;
import hepaestus.com.repository.search.PizzaSearchRepository;
import hepaestus.com.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link hepaestus.com.domain.Pizza}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PizzaResource {

    private final Logger log = LoggerFactory.getLogger(PizzaResource.class);

    private static final String ENTITY_NAME = "pizza";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PizzaRepository pizzaRepository;

    private final PizzaSearchRepository pizzaSearchRepository;

    public PizzaResource(PizzaRepository pizzaRepository, PizzaSearchRepository pizzaSearchRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaSearchRepository = pizzaSearchRepository;
    }

    /**
     * {@code POST  /pizzas} : Create a new pizza.
     *
     * @param pizza the pizza to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pizza, or with status {@code 400 (Bad Request)} if the pizza has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pizzas")
    public ResponseEntity<Pizza> createPizza(@Valid @RequestBody Pizza pizza) throws URISyntaxException {
        log.debug("REST request to save Pizza : {}", pizza);
        if (pizza.getId() != null) {
            throw new BadRequestAlertException("A new pizza cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pizza result = pizzaRepository.save(pizza);
        pizzaSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pizzas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pizzas} : Updates an existing pizza.
     *
     * @param pizza the pizza to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pizza,
     * or with status {@code 400 (Bad Request)} if the pizza is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pizza couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pizzas")
    public ResponseEntity<Pizza> updatePizza(@Valid @RequestBody Pizza pizza) throws URISyntaxException {
        log.debug("REST request to update Pizza : {}", pizza);
        if (pizza.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Pizza result = pizzaRepository.save(pizza);
        pizzaSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pizza.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /pizzas} : get all the pizzas.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pizzas in body.
     */
    @GetMapping("/pizzas")
    public List<Pizza> getAllPizzas() {
        log.debug("REST request to get all Pizzas");
        return pizzaRepository.findAll();
    }

    /**
     * {@code GET  /pizzas/:id} : get the "id" pizza.
     *
     * @param id the id of the pizza to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pizza, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pizzas/{id}")
    public ResponseEntity<Pizza> getPizza(@PathVariable Long id) {
        log.debug("REST request to get Pizza : {}", id);
        Optional<Pizza> pizza = pizzaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pizza);
    }

    /**
     * {@code DELETE  /pizzas/:id} : delete the "id" pizza.
     *
     * @param id the id of the pizza to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pizzas/{id}")
    public ResponseEntity<Void> deletePizza(@PathVariable Long id) {
        log.debug("REST request to delete Pizza : {}", id);
        pizzaRepository.deleteById(id);
        pizzaSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/pizzas?query=:query} : search for the pizza corresponding
     * to the query.
     *
     * @param query the query of the pizza search.
     * @return the result of the search.
     */
    @GetMapping("/_search/pizzas")
    public List<Pizza> searchPizzas(@RequestParam String query) {
        log.debug("REST request to search Pizzas for query {}", query);
        return StreamSupport
            .stream(pizzaSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
