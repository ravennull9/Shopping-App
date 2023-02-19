package shoppingApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shoppingApp.secutiry.JwtFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
// if using annotation method for endpoint protection, uncomment @PreAuthorize in ContentController and @EnableGlobalMethodSecurity here
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtFilter jwtFilter;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/addProduct").hasAuthority("admin") //add new product
                .antMatchers("/update/**").hasAuthority("admin") //restock/change price of the product
                .antMatchers("/allOrders").hasAuthority("admin") //see all orders
                .antMatchers("/allOrders/**").hasAuthority("admin") //confirm or cancel or look into an order
                .antMatchers("/orders").hasAnyAuthority("user", "admin") //see all user-specified orders
                .antMatchers("/orders/**").hasAnyAuthority("user", "admin")
                .antMatchers("/products").hasAnyAuthority("user", "admin")
                .antMatchers("/products/watch").hasAnyAuthority("user", "admin")
                .antMatchers("/allProducts").hasAnyAuthority("admin")
                .antMatchers("/allProducts/**").hasAnyAuthority("admin")
                .anyRequest().authenticated()
                ;
    }
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }


}
