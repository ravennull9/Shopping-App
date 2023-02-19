package shoppingApp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "description")
    private String description;

    @PositiveOrZero
    @Column(name = "quantity")
    private Integer quantity;

    @Positive
    @Column(name = "wholesale_price")
    private Float wholesale_price;

    @Positive
    @Column(name = "retail_price")
    private Float retail_price;

    @NotBlank
    @Column(name = "product_name")
    private String product_name;

}
