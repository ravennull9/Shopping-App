package shoppingApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shoppingApp.DAO.UserDAO;
import shoppingApp.DAO.WatchedProductDAO;
import shoppingApp.domain.Order;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;
import shoppingApp.domain.Watched_Product;
import shoppingApp.exception.InvalidCredentialsException;
import shoppingApp.secutiry.AuthUserDetail;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private UserDAO userDAO;
    private WatchedProductDAO watchedProductDAO;

    @Autowired
    public UserService(UserDAO userDAO, WatchedProductDAO watchedProductDAO){
        this.userDAO = userDAO;
        this.watchedProductDAO = watchedProductDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDAO.getUserByUsername(username);

        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("Username does not exist");
        }

        User user = userOptional.get(); // database user

        return AuthUserDetail.builder() // spring security's userDetail
                .username(user.getUsername())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();
        userAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        return userAuthorities;
    }

    public void addOrder(Order order, String username){
        User user = userDAO.getUserByUsername(username).get();
        user.addOrder(order);
        userDAO.updateUser(user);
    }
    public void addUser(User user, String role){
        userDAO.addUser(user, role);
    }


    public List<User> getAllUser(){
        return userDAO.getAllUser();
    }

    public Optional<User> getUserByUsername(String username){
        return userDAO.getUserByUsername(username);
    }

    public Optional<User> getUserById(int id){ return userDAO.getUserById(id); }

    public Optional<User> getUserByEmail(String email){
        return userDAO.getUserByEmail(email);
    }

    public void updateUser(User user){ userDAO.updateUser(user); }

    public void watchProduct(Watched_Product product, String username){
        User user = userDAO.getUserByUsername(username).get();
        user.addWatchedProduct(product);
        userDAO.updateUser(user);
    }

    public void unwatchProduct(Watched_Product product, String username){
        User user = userDAO.getUserByUsername(username).get();
        user.removeWatchedProduct(product);
        userDAO.updateUser(user);
        watchedProductDAO.deleteWatchedProductByUidPid(user.getId(), product.getProduct_id());
    }

    public void invalidLogin() throws InvalidCredentialsException {
        throw new InvalidCredentialsException("invalid login credentials");
    }

}
