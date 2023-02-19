package shoppingApp.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import shoppingApp.domain.Watched_Product;

import java.util.List;

@Getter
@Setter
@Builder
public class WatchProductResponseBody {
    String message;
    List<Watched_Product> watched_productList;
}
