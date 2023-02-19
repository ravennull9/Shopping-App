package shoppingApp.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseBody {
    private String message;
    private String token;
}
