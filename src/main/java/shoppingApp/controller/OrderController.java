package shoppingApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shoppingApp.domain.*;
import shoppingApp.domain.request.OrderRequestBody;
import shoppingApp.domain.response.*;
import shoppingApp.domain.wrapper.*;
import shoppingApp.secutiry.JwtProvider;
import shoppingApp.service.OrderService;
import shoppingApp.service.ProductService;
import shoppingApp.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
    private OrderService orderService;
    private ProductService productService;
    private UserService userService;

    private JwtProvider jwtProvider;

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public OrderController(OrderService orderService, ProductService productService, UserService userService){
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }

    //user sees all available products
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public WrapperProductResponse getProducts(Authentication authentication){
        List<Product> products = productService.getAvailableProducts();
        List<ProductViewWrapper> wrapper = new ArrayList<>();
        for(Product p: products){
            ProductViewWrapper pw = new ProductViewWrapper(p.getId(), p.getRetail_price(), p.getProduct_name());
            wrapper.add(pw);
        }

        return WrapperProductResponse.builder()
                .message("Hello, " + authentication.getName() + ". Here is the list of today's products!")
                .products(wrapper)
                .build();
    }

    @RequestMapping(value = "/products/{product_id}", method = RequestMethod.GET)
    public ProductWrapper getProductById(Authentication authentication, @PathVariable int product_id){
        Product product = productService.getAvailableProductById(product_id);
        if(product == null) {
            return null;
        }
        ProductWrapper wrapper = new ProductWrapper(product_id, product.getDescription(), product.getRetail_price(), product.getProduct_name());

        return wrapper;
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public OrderResponseBody buyProducts(Authentication authentication, @RequestBody OrderRequestBody orderRequestBody){
        if(orderRequestBody.getProductIds().isEmpty() || orderRequestBody.getQuantities().isEmpty()){
            return OrderResponseBody.builder()
                    .message("Unsuccessful purchase: you can't have empty product/quantity list for the buying action")
                    .order(null)
                    .build();
        }
        if(orderRequestBody.getProductIds().size() != orderRequestBody.getQuantities().size())
            return OrderResponseBody.builder()
                    .message("Unsuccessful purchase: the number of products doesn't match the number of quantities")
                    .order(null)
                    .build();

        String username = (String) authentication.getPrincipal();

        //create new order
        Order order = new Order();
        order.setOrder_date(new java.sql.Timestamp(new java.util.Date().getTime()));
        order.setStatus("Processing");
        float subtotal = (float) 0;
        for(int i = 0; i < orderRequestBody.getProductIds().size(); i++){
            Product product = productService.getProductById(orderRequestBody.getProductIds().get(i));
            Integer quantity = orderRequestBody.getQuantities().get(i);
            if(productService.buyProduct(product, quantity)){
                Ordered_Product ordered_product = new Ordered_Product();
                ordered_product.setExecute_retail_price(product.getRetail_price());
                ordered_product.setExecute_wholesale_price(product.getWholesale_price());
                ordered_product.setProductId(product.getId());
                ordered_product.setQuantity(quantity);
                order.addOrdered_Product(ordered_product);
                subtotal += quantity * product.getRetail_price();
            }
            else return OrderResponseBody.builder()
                    .message("Unsuccessful purchase: the product with id=" + orderRequestBody.getProductIds().get(i) + " is running low on stock.")
                    .order(null)
                    .build();
        }

        order.setSubtotal(subtotal);
        orderService.placeOrder(order);
        userService.addOrder(order, username);
        return OrderResponseBody.builder()
                .message("You've successfully placed an order")
                .order(order)
                .build();
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public List<Order> getOrders(Authentication authentication){
        String username = (String) authentication.getPrincipal();
        User user = userService.getUserByUsername(username).get();
        return orderService.getOrdersByUId(user.getId());
    }

    @RequestMapping(value = "/orders/complete", method = RequestMethod.GET)
    public List<Order> getCompleteOrders(Authentication authentication){
        String username = (String) authentication.getPrincipal();
        User user = userService.getUserByUsername(username).get();
        return orderService.getAllOrdersWithStatus("Complete", user.getId());
    }

    @RequestMapping(value = "/orders/ongoing", method = RequestMethod.GET)
    public List<Order> getOngoingOrders(Authentication authentication){
        String username = (String) authentication.getPrincipal();
        User user = userService.getUserByUsername(username).get();
        return orderService.getAllOrdersWithStatus("Processing", user.getId());
    }

    @RequestMapping(value = "/orders/canceled", method = RequestMethod.GET)
    public List<Order> getCanceledOrders(Authentication authentication){
        String username = (String) authentication.getPrincipal();
        User user = userService.getUserByUsername(username).get();
        return orderService.getAllOrdersWithStatus("Canceled", user.getId());
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
    public List<OrderedProductViewWrapper> getOrderById(Authentication authentication, @PathVariable int orderId){
        String username = (String) authentication.getPrincipal();
        User user = userService.getUserByUsername(username).get();
        Order order = orderService.getOrderById(orderId);
        if(order == null || !orderService.hasOrder(user.getId(), orderId)){
            return null;
        }
        OrderedProductViewWrapper opw = new OrderedProductViewWrapper();
        return opw.convertFromOrderedProducts(orderService.getOrderedProductByOrderId(orderId));
    }

    @RequestMapping(value = "/orders/top3freq", method = RequestMethod.GET)
    public List<ProductQuantityWrapper> getTop3Freq(Authentication authentication){
        User user = userService.getUserByUsername(authentication.getName()).get();
        List<ProductQuantityWrapper> res = orderService.topThreeFrequent(user.getId());
        for(ProductQuantityWrapper pqw: res){
            String name = productService.getProductById(pqw.getProduct_id()).getProduct_name();
            pqw.setName(name);
        }
        return res;
    }

    @RequestMapping(value = "/orders/top3recent", method = RequestMethod.GET)
    public List<ProductIdWrapper> getTop3Recent(Authentication authentication){
        User user = userService.getUserByUsername(authentication.getName()).get();
        return orderService.topThreeRecent(user.getId());
    }

    @RequestMapping(value = "/orders/cancel/{orderId}", method = RequestMethod.POST)
    public OrderResponseBody cancelOrder(@PathVariable int orderId, Authentication authentication){
        Order order = orderService.getOrderById(orderId);
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).get();

        //invalid order number or other user's order
        if(order == null || !orderService.hasOrder(user.getId(), orderId))
            return OrderResponseBody.builder()
                .message("Order cancellation failed: order doesn't exist")
                .order(null)
                .build();

        if(order.getStatus().equals("Complete"))
            return OrderResponseBody.builder()
                    .message("Order cancellation failed: the order has already completed")
                    .order(order)
                    .build();
        if(order.getStatus().equals("Canceled"))
            return OrderResponseBody.builder()
                    .message("Order cancellation failed: the order has already been canceled, no need to cancel it")
                    .order(order)
                    .build();

        for(Ordered_Product op: orderService.getOrderedProductByOrderId(orderId)){
            Integer productId = op.getProductId();
            Integer quantity = op.getQuantity();
            Product product = productService.getProductById(productId);
            product.setQuantity(product.getQuantity()+quantity);
            productService.updateProduct(product);
        }
        orderService.changeOrderStatus(order, "Canceled");
        return OrderResponseBody.builder()
                .message("Order has been successfully canceled!")
                .order(order)
                .build();
    }

}
