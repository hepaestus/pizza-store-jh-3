package hepaestus.com.web.rest;

import hepaestus.com.PizzastoreApp;
import hepaestus.com.domain.Pizza;
import hepaestus.com.repository.PizzaRepository;
import hepaestus.com.repository.search.PizzaSearchRepository;
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
import org.springframework.util.Base64Utils;
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

import hepaestus.com.domain.enumeration.Size;
/**
 * Integration tests for the {@link PizzaResource} REST controller.
 */
@SpringBootTest(classes = PizzastoreApp.class)
public class PizzaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Size DEFAULT_SIZE = Size.S;
    private static final Size UPDATED_SIZE = Size.M;

    @Autowired
    private PizzaRepository pizzaRepository;

    /**
     * This repository is mocked in the hepaestus.com.repository.search test package.
     *
     * @see hepaestus.com.repository.search.PizzaSearchRepositoryMockConfiguration
     */
    @Autowired
    private PizzaSearchRepository mockPizzaSearchRepository;

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

    private MockMvc restPizzaMockMvc;

    private Pizza pizza;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PizzaResource pizzaResource = new PizzaResource(pizzaRepository, mockPizzaSearchRepository);
        this.restPizzaMockMvc = MockMvcBuilders.standaloneSetup(pizzaResource)
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
    public static Pizza createEntity(EntityManager em) {
        Pizza pizza = new Pizza()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .size(DEFAULT_SIZE);
        return pizza;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pizza createUpdatedEntity(EntityManager em) {
        Pizza pizza = new Pizza()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .size(UPDATED_SIZE);
        return pizza;
    }

    @BeforeEach
    public void initTest() {
        pizza = createEntity(em);
    }

    @Test
    @Transactional
    public void createPizza() throws Exception {
        int databaseSizeBeforeCreate = pizzaRepository.findAll().size();

        // Create the Pizza
        restPizzaMockMvc.perform(post("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pizza)))
            .andExpect(status().isCreated());

        // Validate the Pizza in the database
        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeCreate + 1);
        Pizza testPizza = pizzaList.get(pizzaList.size() - 1);
        assertThat(testPizza.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPizza.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPizza.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testPizza.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPizza.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPizza.getSize()).isEqualTo(DEFAULT_SIZE);

        // Validate the Pizza in Elasticsearch
        verify(mockPizzaSearchRepository, times(1)).save(testPizza);
    }

    @Test
    @Transactional
    public void createPizzaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pizzaRepository.findAll().size();

        // Create the Pizza with an existing ID
        pizza.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPizzaMockMvc.perform(post("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pizza)))
            .andExpect(status().isBadRequest());

        // Validate the Pizza in the database
        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeCreate);

        // Validate the Pizza in Elasticsearch
        verify(mockPizzaSearchRepository, times(0)).save(pizza);
    }


    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = pizzaRepository.findAll().size();
        // set the field null
        pizza.setPrice(null);

        // Create the Pizza, which fails.

        restPizzaMockMvc.perform(post("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pizza)))
            .andExpect(status().isBadRequest());

        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pizzaRepository.findAll().size();
        // set the field null
        pizza.setSize(null);

        // Create the Pizza, which fails.

        restPizzaMockMvc.perform(post("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pizza)))
            .andExpect(status().isBadRequest());

        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPizzas() throws Exception {
        // Initialize the database
        pizzaRepository.saveAndFlush(pizza);

        // Get all the pizzaList
        restPizzaMockMvc.perform(get("/api/pizzas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pizza.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }
    
    @Test
    @Transactional
    public void getPizza() throws Exception {
        // Initialize the database
        pizzaRepository.saveAndFlush(pizza);

        // Get the pizza
        restPizzaMockMvc.perform(get("/api/pizzas/{id}", pizza.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pizza.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPizza() throws Exception {
        // Get the pizza
        restPizzaMockMvc.perform(get("/api/pizzas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePizza() throws Exception {
        // Initialize the database
        pizzaRepository.saveAndFlush(pizza);

        int databaseSizeBeforeUpdate = pizzaRepository.findAll().size();

        // Update the pizza
        Pizza updatedPizza = pizzaRepository.findById(pizza.getId()).get();
        // Disconnect from session so that the updates on updatedPizza are not directly saved in db
        em.detach(updatedPizza);
        updatedPizza
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .size(UPDATED_SIZE);

        restPizzaMockMvc.perform(put("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPizza)))
            .andExpect(status().isOk());

        // Validate the Pizza in the database
        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeUpdate);
        Pizza testPizza = pizzaList.get(pizzaList.size() - 1);
        assertThat(testPizza.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPizza.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPizza.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPizza.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPizza.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPizza.getSize()).isEqualTo(UPDATED_SIZE);

        // Validate the Pizza in Elasticsearch
        verify(mockPizzaSearchRepository, times(1)).save(testPizza);
    }

    @Test
    @Transactional
    public void updateNonExistingPizza() throws Exception {
        int databaseSizeBeforeUpdate = pizzaRepository.findAll().size();

        // Create the Pizza

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPizzaMockMvc.perform(put("/api/pizzas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pizza)))
            .andExpect(status().isBadRequest());

        // Validate the Pizza in the database
        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pizza in Elasticsearch
        verify(mockPizzaSearchRepository, times(0)).save(pizza);
    }

    @Test
    @Transactional
    public void deletePizza() throws Exception {
        // Initialize the database
        pizzaRepository.saveAndFlush(pizza);

        int databaseSizeBeforeDelete = pizzaRepository.findAll().size();

        // Delete the pizza
        restPizzaMockMvc.perform(delete("/api/pizzas/{id}", pizza.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pizza> pizzaList = pizzaRepository.findAll();
        assertThat(pizzaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Pizza in Elasticsearch
        verify(mockPizzaSearchRepository, times(1)).deleteById(pizza.getId());
    }

    @Test
    @Transactional
    public void searchPizza() throws Exception {
        // Initialize the database
        pizzaRepository.saveAndFlush(pizza);
        when(mockPizzaSearchRepository.search(queryStringQuery("id:" + pizza.getId())))
            .thenReturn(Collections.singletonList(pizza));
        // Search the pizza
        restPizzaMockMvc.perform(get("/api/_search/pizzas?query=id:" + pizza.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pizza.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }
}
