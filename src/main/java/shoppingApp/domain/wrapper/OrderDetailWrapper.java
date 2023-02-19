package shoppingApp.domain.wrapper;

import lombok.*;
import shoppingApp.domain.Ordered_Product;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDetailWrapper {
    String username;
    List<Ordered_Product> ordered_products;
}
