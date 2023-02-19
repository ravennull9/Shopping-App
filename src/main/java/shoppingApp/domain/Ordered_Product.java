package shoppingApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name="ordered_products")
public class Ordered_Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "productId")
    private Integer productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "execute_retail_price")
    private Float execute_retail_price;

    @Column(name = "execute_wholesale_price")
    private Float execute_wholesale_price;

    @ManyToOne
    @JoinColumn(name = "fk_orderId")
    @JsonIgnore
    @ToString.Exclude
    private Order order;
}
