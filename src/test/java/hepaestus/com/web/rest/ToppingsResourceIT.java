package hepaestus.com.web.rest;

import hepaestus.com.PizzastoreApp;
import hepaestus.com.domain.Toppings;
import hepaestus.com.repository.ToppingsRepository;
import hepaestus.com.repository.search.ToppingsSearchRepository;
import hepaestus.com.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static hepaestus.com.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ToppingsResource} REST controller.
 */
@SpringBootTest(classes = PizzastoreApp.class)
public class ToppingsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    @Autowired
    private ToppingsRepository toppingsRepository;

    /**
     * This repository is mocked in the hepaestus.com.repository.search test package.
     *
     * @see hepaestus.com.repository.search.ToppingsSearchRepositoryMockConfiguration
     */
    @Autowired
    private ToppingsSearchRepository mockToppingsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restToppingsMockMvc;

    private Toppings toppings;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ToppingsResource toppingsResource = new ToppingsResource(toppingsRepository, mockToppingsSearchRepository);
        this.restToppingsMockMvc = MockMvcBuilders.standaloneSetup(toppingsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toppings createEntity(EntityManager em) {
        Toppings toppings = new Toppings()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE);
        return toppings;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toppings createUpdatedEntity(EntityManager em) {
        Toppings toppings = new Toppings()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE);
        return toppings;
    }

    @BeforeEach
    public void initTest() {
        toppings = createEntity(em);
    }

    @Test
    @Transactional
    public void createToppings() throws Exception {
        int databaseSizeBeforeCreate = toppingsRepository.findAll().size();

        // Create the Toppings
        restToppingsMockMvc.perform(post("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toppings)))
            .andExpect(status().isCreated());

        // Validate the Toppings in the database
        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeCreate + 1);
        Toppings testToppings = toppingsList.get(toppingsList.size() - 1);
        assertThat(testToppings.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testToppings.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testToppings.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the Toppings in Elasticsearch
        verify(mockToppingsSearchRepository, times(1)).save(testToppings);
    }

    @Test
    @Transactional
    public void createToppingsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toppingsRepository.findAll().size();

        // Create the Toppings with an existing ID
        toppings.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToppingsMockMvc.perform(post("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toppings)))
            .andExpect(status().isBadRequest());

        // Validate the Toppings in the database
        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Toppings in Elasticsearch
        verify(mockToppingsSearchRepository, times(0)).save(toppings);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = toppingsRepository.findAll().size();
        // set the field null
        toppings.setName(null);

        // Create the Toppings, which fails.

        restToppingsMockMvc.perform(post("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toppings)))
            .andExpect(status().isBadRequest());

        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = toppingsRepository.findAll().size();
        // set the field null
        toppings.setPrice(null);

        // Create the Toppings, which fails.

        restToppingsMockMvc.perform(post("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toppings)))
            .andExpect(status().isBadRequest());

        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllToppings() throws Exception {
        // Initialize the database
        toppingsRepository.saveAndFlush(toppings);

        // Get all the toppingsList
        restToppingsMockMvc.perform(get("/api/toppings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toppings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }
    
    @Test
    @Transactional
    public void getToppings() throws Exception {
        // Initialize the database
        toppingsRepository.saveAndFlush(toppings);

        // Get the toppings
        restToppingsMockMvc.perform(get("/api/toppings/{id}", toppings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(toppings.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingToppings() throws Exception {
        // Get the toppings
        restToppingsMockMvc.perform(get("/api/toppings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToppings() throws Exception {
        // Initialize the database
        toppingsRepository.saveAndFlush(toppings);

        int databaseSizeBeforeUpdate = toppingsRepository.findAll().size();

        // Update the toppings
        Toppings updatedToppings = toppingsRepository.findById(toppings.getId()).get();
        // Disconnect from session so that the updates on updatedToppings are not directly saved in db
        em.detach(updatedToppings);
        updatedToppings
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE);

        restToppingsMockMvc.perform(put("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedToppings)))
            .andExpect(status().isOk());

        // Validate the Toppings in the database
        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeUpdate);
        Toppings testToppings = toppingsList.get(toppingsList.size() - 1);
        assertThat(testToppings.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testToppings.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testToppings.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the Toppings in Elasticsearch
        verify(mockToppingsSearchRepository, times(1)).save(testToppings);
    }

    @Test
    @Transactional
    public void updateNonExistingToppings() throws Exception {
        int databaseSizeBeforeUpdate = toppingsRepository.findAll().size();

        // Create the Toppings

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToppingsMockMvc.perform(put("/api/toppings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toppings)))
            .andExpect(status().isBadRequest());

        // Validate the Toppings in the database
        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Toppings in Elasticsearch
        verify(mockToppingsSearchRepository, times(0)).save(toppings);
    }

    @Test
    @Transactional
    public void deleteToppings() throws Exception {
        // Initialize the database
        toppingsRepository.saveAndFlush(toppings);

        int databaseSizeBeforeDelete = toppingsRepository.findAll().size();

        // Delete the toppings
        restToppingsMockMvc.perform(delete("/api/toppings/{id}", toppings.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Toppings> toppingsList = toppingsRepository.findAll();
        assertThat(toppingsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Toppings in Elasticsearch
        verify(mockToppingsSearchRepository, times(1)).deleteById(toppings.getId());
    }

    @Test
    @Transactional
    public void searchToppings() throws Exception {
        // Initialize the database
        toppingsRepository.saveAndFlush(toppings);
        when(mockToppingsSearchRepository.search(queryStringQuery("id:" + toppings.getId())))
            .thenReturn(Collections.singletonList(toppings));
        // Search the toppings
        restToppingsMockMvc.perform(get("/api/_search/toppings?query=id:" + toppings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toppings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }
}
