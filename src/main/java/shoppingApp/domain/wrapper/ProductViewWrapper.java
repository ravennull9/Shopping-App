package shoppingApp.domain.wrapper;


import lombok.*;
import shoppingApp.domain.Ordered_Product;
import shoppingApp.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductViewWrapper {
    private Integer id;

    private Float price;

    private String product_name;
}
