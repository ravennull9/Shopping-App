package shoppingApp.domain.wrapper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSubtotalWrapper {
    private String username;
    private float total_spend;
}
