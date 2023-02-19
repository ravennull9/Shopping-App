package shoppingApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "username should not be blank")
    @Column(name = "username")
    private String username;

    @Email(message = "email should should have correct format")
    @Column(name = "email")
    private String email;

    @Size(min=8, message = "password should be at least 8 character long")
    @NotBlank(message = "password should not be blank")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Watched_Product> watched_products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    public void addOrder(Order order){
        this.orders.add(order);
        order.setUser(this);
    }

    public void addWatchedProduct(Watched_Product watched_product){
        this.watched_products.add(watched_product);
        watched_product.setUser(this);
    }

    public void removeWatchedProduct(Watched_Product watched_product){
        for(Watched_Product wp: watched_products){
            if(wp.getId() == watched_product.getId()) {
                watched_products.remove(wp);
                return;
            }
        }
    }
}
