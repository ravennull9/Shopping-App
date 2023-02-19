package shoppingApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;
import shoppingApp.domain.Watched_Product;
import shoppingApp.domain.response.WatchProductResponseBody;
import shoppingApp.domain.response.WrapperProductResponse;
import shoppingApp.service.ProductService;
import shoppingApp.service.UserService;
import shoppingApp.service.WatchListService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class WatchListController {
    private ProductService productService;
    private UserService userService;
    private WatchListService watchListService;

    @Autowired
    public WatchListController(ProductService productService, UserService userService, WatchListService watchListService){
        this.productService = productService;
        this.userService = userService;
        this.watchListService = watchListService;
    }

    @RequestMapping(value = "/products/watch", method = RequestMethod.POST)
    public WatchProductResponseBody watchProductById(Authentication authentication, @RequestParam List<Integer> productIds){
        String username = authentication.getName();
        List<Product> products = new ArrayList<>();
        User user = userService.getUserByUsername(username).get();

        StringBuilder sb = new StringBuilder();

        //product validation
        for(int id: productIds){
            Product product = productService.getProductById(id);
            Optional<Watched_Product> watched_product = watchListService.getWatchedProductByProductId(id, user.getId());
            if(product==null){
                return WatchProductResponseBody.builder().
                        message("The product id " + id + " is invalid!").
                        watched_productList(null).
                        build();
            }
            if(watched_product != null && watched_product.isPresent()){
                sb.append("product with id=" + id + " already exist in your watch list, action has no effect.");
            }else{
                products.add(product);
            }
        }

        List<Watched_Product> watched_productList = new ArrayList<>();
        for(Product p: products){
            Watched_Product product = new Watched_Product();
            product.setProduct_id(p.getId());
            userService.watchProduct(product, username);
            watched_productList.add(product);
        }
        sb.append("You've successfully watched the products!");
        return WatchProductResponseBody.builder().
                message(sb.toString()).
                watched_productList(watched_productList).
                build();
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.GET)
    public WrapperProductResponse getWatchList(Authentication authentication){
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).get();
        List<Watched_Product> watched_productList = watchListService.getWatchedProductsByUid(user.getId());
        return WrapperProductResponse.builder()
                .message("Hello " + username + ", here is a list of your watched products")
                .products(watchListService.getProductViewWrappers(watched_productList)).build();
    }


    @RequestMapping(value = "/watchlist", method = RequestMethod.DELETE)
    public String deleteWatchedItems(Authentication authentication, @RequestParam List<Integer> productIds){
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).get();
        List<Watched_Product> unWatchList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(Integer productId: productIds){
            Optional<Watched_Product> possibleProduct = watchListService.getWatchedProductsByUidPid(user.getId(), productId);
            if(possibleProduct != null && possibleProduct.isPresent()){
                unWatchList.add(possibleProduct.get());
            }
            else{
                sb.append("Note: productId=" + productId + " is not in your watch list, unwatch action has no effect");
            }
        }
        for(Watched_Product wp: unWatchList){
            userService.unwatchProduct(wp, username);
        }
        sb.append("You've successfully unwatched the products!");
        return sb.toString();
    }


}
