package hepaestus.com.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A Invoice.
 */
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "invoice")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "orderplaced")
    private LocalDate orderplaced;

    @OneToMany(mappedBy = "invoice")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pizza> pizzas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public Invoice phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getOrderplaced() {
        return orderplaced;
    }

    public Invoice orderplaced(LocalDate orderplaced) {
        this.orderplaced = orderplaced;
        return this;
    }

    public void setOrderplaced(LocalDate orderplaced) {
        this.orderplaced = orderplaced;
    }

    public Set<Pizza> getPizzas() {
        return pizzas;
    }

    public Invoice pizzas(Set<Pizza> pizzas) {
        this.pizzas = pizzas;
        return this;
    }

    public Invoice addPizza(Pizza pizza) {
        this.pizzas.add(pizza);
        pizza.setInvoice(this);
        return this;
    }

    public Invoice removePizza(Pizza pizza) {
        this.pizzas.remove(pizza);
        pizza.setInvoice(null);
        return this;
    }

    public void setPizzas(Set<Pizza> pizzas) {
        this.pizzas = pizzas;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return id != null && id.equals(((Invoice) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", orderplaced='" + getOrderplaced() + "'" +
            "}";
    }
}
