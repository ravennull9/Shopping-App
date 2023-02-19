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
@Table(name="watched_products")
public class Watched_Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name = "product_id")
    private Integer product_id;

    @ManyToOne
    @JoinColumn(name = "fk_userId")
    @JsonIgnore
    @ToString.Exclude
    private User user;
}
