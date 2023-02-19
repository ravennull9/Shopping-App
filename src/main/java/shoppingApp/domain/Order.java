package shoppingApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.core.Ordered;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_date")
    private Timestamp order_date;

    @Column(name = "status")
    private String status;

    @Column(name = "subtotal")
    private Float subtotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Ordered_Product> products = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fk_userId")
    @JsonIgnore
    @ToString.Exclude
    private User user;

    public void addOrdered_Product(Ordered_Product ordered_product){
        this.products.add(ordered_product);
        ordered_product.setOrder(this);
    }
}
