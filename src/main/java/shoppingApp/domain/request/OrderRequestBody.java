package shoppingApp.domain.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestBody {
    private List<Integer> productIds;
    private List<Integer> quantities;
}
