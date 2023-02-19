package shoppingApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import shoppingApp.DAO.ProductDAO;
import shoppingApp.domain.Order;
import shoppingApp.domain.Ordered_Product;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;
import shoppingApp.domain.response.OrderResponseBody;
import shoppingApp.domain.wrapper.*;
import shoppingApp.service.AdminService;
import shoppingApp.service.OrderService;
import shoppingApp.service.ProductService;
import shoppingApp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    private ProductService productService;
    private AdminService adminService;
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public AdminController(ProductService productService, AdminService adminService,
                           OrderService orderService, UserService userService){
        this.productService = productService;
        this.adminService = adminService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.PUT)
    public Product addProduct(@RequestBody @Valid Product product){
        return productService.getProductById(adminService.addProduct(product));
    }

    @RequestMapping(value = "/update/restock/{product_id}", method = RequestMethod.PUT)
    public Product restockProduct(@PathVariable int product_id, @RequestParam int quantity){
        productService.restockProductById(product_id, quantity);
        return productService.getProductById(product_id);
    }

    @RequestMapping(value = "/update/retail_price/{product_id}", method = RequestMethod.PUT)
    public Product updateRetailPrice(@PathVariable int product_id, @RequestParam float retail_price){
        productService.changeRetailPriceById(product_id, retail_price);
        return productService.getProductById(product_id);
    }

    @RequestMapping(value = "/update/whole_sale_price/{product_id}", method = RequestMethod.PUT)
    public Product updateWholeSalePrice(@PathVariable int product_id, @RequestParam float whole_sale_price){
        productService.changeWholeSalePriceById(product_id, whole_sale_price);
        return productService.getProductById(product_id);
    }

    @RequestMapping(value = "/allOrders/top3Buyers", method = RequestMethod.GET)
    public List<UserSubtotalWrapper> getTop3Buyers(){
        Map<Integer, Float> top3 = orderService.getTop3Buyers();
        List<UserSubtotalWrapper> top3Buyers = new ArrayList<>();
        for(int uid: top3.keySet()){
            User user = userService.getUserById(uid).get();
            UserSubtotalWrapper usw = new UserSubtotalWrapper(user.getUsername(), top3.get(uid));
            top3Buyers.add(usw);
        }
        return top3Buyers;
    }

    @RequestMapping(value = "/allOrders/productSales", method = RequestMethod.GET)
    public TotalSaleCountWrapper getProductSales(){
        List<ProductQuantityWrapper> res = orderService.getProductSales();
        int total_counts = 0;
        for(ProductQuantityWrapper pqw: res){
            String name = productService.getProductById(pqw.getProduct_id()).getProduct_name();
            pqw.setName(name);
            total_counts += pqw.getQuantity();
        }
        return new TotalSaleCountWrapper(res, total_counts);
    }

    @RequestMapping(value = "/allOrders/top3Popular", method = RequestMethod.GET)
    public List<ProductQuantityWrapper> getTop3Popular(){
        List<ProductQuantityWrapper> res = orderService.getTop3Popular();
        for(ProductQuantityWrapper pqw: res){
            String name = productService.getProductById(pqw.getProduct_id()).getProduct_name();
            pqw.setName(name);
        }
        return res;
    }

    @RequestMapping(value = "/allOrders/topProfit", method = RequestMethod.GET)
    public ProductProfitWrapper getTopProfitProduct(){
        ProductProfitWrapper ppw = orderService.getTopProfitProduct();
        String name = productService.getProductById(ppw.getProduct_id()).getProduct_name();
        ppw.setProduct_name(name);
        return ppw;
    }

    /*
    *
    * view orders, view a specific order, confirm/cancel an order
    *
    */

    @RequestMapping(value = "/allOrders", method = RequestMethod.GET)
    public List<Order> getOrders(){
        return orderService.getAllOrders();
    }

    @RequestMapping(value = "/allOrders/ongoing", method = RequestMethod.GET)
    public List<Order> getOngoingOrders(){
        return orderService.getAllOrdersWithStatus("Processing");
    }

    @RequestMapping(value = "/allOrders/complete", method = RequestMethod.GET)
    public List<Order> getCompleteOrders(){
        return orderService.getAllOrdersWithStatus("Complete");
    }

    @RequestMapping(value = "/allOrders/canceled", method = RequestMethod.GET)
    public List<Order> getCanceledOrders(){
        return orderService.getAllOrdersWithStatus("Canceled");
    }

    @RequestMapping(value = "/allOrders/{orderId}", method = RequestMethod.GET)
    public OrderDetailWrapper getOrderById(@PathVariable int orderId){
        Order order = orderService.getOrderById(orderId);
        if(order == null){
            return null;
        }
        OrderDetailWrapper odw = new OrderDetailWrapper(
                order.getUser().getUsername(),
                orderService.getOrderedProductByOrderId(orderId));
        return odw;
    }

    @RequestMapping(value = "/allOrders/confirm/{orderId}", method = RequestMethod.POST)
    public OrderResponseBody confirmOrder(@PathVariable int orderId){
        Order order = orderService.getOrderById(orderId);
        if(order == null)
            return OrderResponseBody.builder()
                    .message("Order confirmation failed: order doesn't exist")
                    .order(null)
                    .build();
        if(order.getStatus().equals("Canceled"))
            return OrderResponseBody.builder()
                    .message("Order confirmation failed: you cannot confirm a canceled order")
                    .order(order)
                    .build();
        if(order.getStatus().equals("Complete"))
            return OrderResponseBody.builder()
                    .message("Order confirmation failed: the order has already been completed, no need to confirm it")
                    .order(order)
                    .build();
        orderService.changeOrderStatus(order, "Complete");
        return OrderResponseBody.builder()
                .message("Order has been successfully confirmed!")
                .order(order)
                .build();
    }

    @RequestMapping(value = "/allOrders/cancel/{orderId}", method = RequestMethod.POST)
    public OrderResponseBody cancelOrder(@PathVariable int orderId){
        Order order = orderService.getOrderById(orderId);

        //invalid order number
        if(order == null)
            return OrderResponseBody.builder()
                    .message("Order cancellation failed: order doesn't exist")
                    .order(null)
                    .build();

        if(order.getStatus().equals("Complete"))
            return OrderResponseBody.builder()
                    .message("Order cancellation failed: the order has already been completed")
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

    @RequestMapping(value = "/allProducts/{product_id}", method = RequestMethod.GET)
    public ProductWrapper getAllProductById(Authentication authentication, @PathVariable int product_id){
        Product product = productService.getProductById(product_id);
        if(product == null) {
            return null;
        }
        ProductWrapper wrapper = new ProductWrapper(product_id, product.getDescription(), product.getRetail_price(), product.getProduct_name());

        return wrapper;
    }

    //admin
    @RequestMapping(value = "/allProducts", method = RequestMethod.GET)
    public List<Product> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        return products;
    }
}
