package shoppingApp.domain.wrapper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductProfitWrapper {
    private Integer product_id;
    private Float profit;
    private String product_name;
}
