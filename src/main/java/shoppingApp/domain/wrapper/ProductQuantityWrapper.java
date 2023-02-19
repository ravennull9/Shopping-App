package shoppingApp.domain.wrapper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductQuantityWrapper {
    private Integer product_id;
    private Integer quantity;
    private String name;
}
