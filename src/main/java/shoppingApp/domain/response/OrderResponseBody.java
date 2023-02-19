package shoppingApp.domain.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import shoppingApp.domain.Order;
import shoppingApp.domain.Product;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponseBody {
    private String message;
    Order order;
}
