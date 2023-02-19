package shoppingApp.domain.wrapper;

import lombok.*;
import shoppingApp.domain.Ordered_Product;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderedProductViewWrapper {
    private Integer product_id;

    private Float price;

    private Integer quantity;

    public List<OrderedProductViewWrapper> convertFromOrderedProducts(List<Ordered_Product> ops){
        List<OrderedProductViewWrapper> res = new ArrayList<>();
        for(Ordered_Product op: ops){
            res.add(new OrderedProductViewWrapper(op.getProductId(), op.getExecute_retail_price(), op.getQuantity()));
        }
        return res;
    }
}
