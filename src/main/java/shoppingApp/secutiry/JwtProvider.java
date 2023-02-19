package shoppingApp.secutiry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;

    @Value("${security.jwt.token.expirationMs}")
    private int expirationInMs;

    // resolves the token -> use the information in the token to create a userDetail object
    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request){
        String prefixedToken = request.getHeader("Authorization"); // extract token value by key "Authorization"
        if(prefixedToken == null) return null;
        String token = prefixedToken.substring(7); // remove the prefix "Bearer "

        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // decode

        String username = claims.getSubject();
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());

        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .username(username)
                .authorities(authorities)
                .build());
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    public String createToken(UserDetails userDetails){
        //Claims is essentially a key-value pair, where the key is a string and the value is an object
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername()); // user identifier
        claims.put("permissions", userDetails.getAuthorities()); // user permission
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date((new Date()).getTime() + expirationInMs))
                .signWith(SignatureAlgorithm.HS256, key) // algorithm and key to sign the token
                .compact();
    }

}
