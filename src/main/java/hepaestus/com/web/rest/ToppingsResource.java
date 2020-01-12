package hepaestus.com.web.rest;

import hepaestus.com.domain.Toppings;
import hepaestus.com.repository.ToppingsRepository;
import hepaestus.com.repository.search.ToppingsSearchRepository;
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
 * REST controller for managing {@link hepaestus.com.domain.Toppings}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ToppingsResource {

    private final Logger log = LoggerFactory.getLogger(ToppingsResource.class);

    private static final String ENTITY_NAME = "toppings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToppingsRepository toppingsRepository;

    private final ToppingsSearchRepository toppingsSearchRepository;

    public ToppingsResource(ToppingsRepository toppingsRepository, ToppingsSearchRepository toppingsSearchRepository) {
        this.toppingsRepository = toppingsRepository;
        this.toppingsSearchRepository = toppingsSearchRepository;
    }

    /**
     * {@code POST  /toppings} : Create a new toppings.
     *
     * @param toppings the toppings to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new toppings, or with status {@code 400 (Bad Request)} if the toppings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/toppings")
    public ResponseEntity<Toppings> createToppings(@Valid @RequestBody Toppings toppings) throws URISyntaxException {
        log.debug("REST request to save Toppings : {}", toppings);
        if (toppings.getId() != null) {
            throw new BadRequestAlertException("A new toppings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Toppings result = toppingsRepository.save(toppings);
        toppingsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/toppings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /toppings} : Updates an existing toppings.
     *
     * @param toppings the toppings to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toppings,
     * or with status {@code 400 (Bad Request)} if the toppings is not valid,
     * or with status {@code 500 (Internal Server Error)} if the toppings couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/toppings")
    public ResponseEntity<Toppings> updateToppings(@Valid @RequestBody Toppings toppings) throws URISyntaxException {
        log.debug("REST request to update Toppings : {}", toppings);
        if (toppings.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Toppings result = toppingsRepository.save(toppings);
        toppingsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, toppings.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /toppings} : get all the toppings.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of toppings in body.
     */
    @GetMapping("/toppings")
    public List<Toppings> getAllToppings() {
        log.debug("REST request to get all Toppings");
        return toppingsRepository.findAll();
    }

    /**
     * {@code GET  /toppings/:id} : get the "id" toppings.
     *
     * @param id the id of the toppings to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the toppings, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/toppings/{id}")
    public ResponseEntity<Toppings> getToppings(@PathVariable Long id) {
        log.debug("REST request to get Toppings : {}", id);
        Optional<Toppings> toppings = toppingsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(toppings);
    }

    /**
     * {@code DELETE  /toppings/:id} : delete the "id" toppings.
     *
     * @param id the id of the toppings to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/toppings/{id}")
    public ResponseEntity<Void> deleteToppings(@PathVariable Long id) {
        log.debug("REST request to delete Toppings : {}", id);
        toppingsRepository.deleteById(id);
        toppingsSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/toppings?query=:query} : search for the toppings corresponding
     * to the query.
     *
     * @param query the query of the toppings search.
     * @return the result of the search.
     */
    @GetMapping("/_search/toppings")
    public List<Toppings> searchToppings(@RequestParam String query) {
        log.debug("REST request to search Toppings for query {}", query);
        return StreamSupport
            .stream(toppingsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
