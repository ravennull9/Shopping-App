package shoppingApp.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import shoppingApp.domain.wrapper.ProductViewWrapper;

import java.util.List;

@Getter
@Setter
@Builder
public class WrapperProductResponse {
    String message;
    List<ProductViewWrapper> products;
}
