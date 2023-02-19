package shoppingApp.domain.wrapper;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TotalSaleCountWrapper {
    private List<ProductQuantityWrapper> sold_products;
    private Integer totalNumProductSold;
}
