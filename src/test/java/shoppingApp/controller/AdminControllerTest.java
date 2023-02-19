package shoppingApp.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestBody;
import shoppingApp.domain.Order;
import shoppingApp.domain.Ordered_Product;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;
import shoppingApp.domain.wrapper.OrderDetailWrapper;
import shoppingApp.domain.wrapper.ProductWrapper;
import shoppingApp.secutiry.JwtProvider;
import shoppingApp.service.AdminService;
import shoppingApp.service.OrderService;
import shoppingApp.service.ProductService;
import shoppingApp.service.UserService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @Test
    public void testGetProduct() throws Exception{
        List<Product> mockProducts = new ArrayList<>();
        Product mockProduct1 = new Product();
        mockProduct1.setId(1);
        mockProduct1.setDescription("description");
        mockProduct1.setProduct_name("product1");
        mockProduct1.setRetail_price(99.9f);
        Product mockProduct2 = new Product();
        mockProduct2.setId(2);
        mockProduct2.setDescription("description2");
        mockProduct2.setProduct_name("product2");
        mockProduct2.setRetail_price(11.9f);
        mockProducts.add(mockProduct1);
        mockProducts.add(mockProduct2);
        ProductWrapper mockPw = new ProductWrapper(
                mockProduct1.getId(), mockProduct1.getDescription(),
                mockProduct1.getRetail_price(), mockProduct1.getProduct_name());

        when(productService.getProductById(1)).thenReturn(mockProduct1);
        when(productService.getAllProducts()).thenReturn(mockProducts);

        //get product by id
        MvcResult result_getSingle = mockMvc.perform(MockMvcRequestBuilders
                        .get("/allProducts/{product_id}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProductWrapper pw = new Gson().fromJson(result_getSingle.getResponse().getContentAsString(), ProductWrapper.class);
        assertEquals(mockPw.toString(), pw.toString());

        //get all products
        MvcResult result_getAll = mockMvc.perform(MockMvcRequestBuilders
                        .get("/allProducts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
        List<Product> products = new Gson().fromJson(result_getAll.getResponse().getContentAsString(), listType);
        assertEquals(mockProducts.toString(), products.toString());
    }

    @Test
    public void testGetOrderByOrderId() throws Exception {
        Ordered_Product mockOP = Ordered_Product.builder()
                .execute_retail_price(92.1f)
                .execute_wholesale_price(80.1f)
                .productId(1)
                .id(1).build();
        List<Ordered_Product> mockOpList = new ArrayList<>();
        mockOpList.add(mockOP);

        User mockUser = User.builder().username("testUser").build();

        Order mockOrder = Order.builder().
                id(1).
                order_date(new java.sql.Timestamp(new java.util.Date().getTime()))
                .subtotal(92.1f)
                .products(mockOpList).user(mockUser).build();

        OrderDetailWrapper mockOdw = OrderDetailWrapper.builder().
                username("testUser").
                ordered_products(mockOpList).build();

        when(orderService.getOrderById(1)).thenReturn(mockOrder);
        when(orderService.getOrderedProductByOrderId(1)).thenReturn(mockOpList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/allOrders/{orderId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderDetailWrapper odw = new Gson().fromJson(result.getResponse().getContentAsString(), OrderDetailWrapper.class);
        assertEquals(mockOdw.toString(), odw.toString());
    }

    @Test
    public void testAddProduct() throws Exception {
        when(productService.getProductById(any())).thenReturn(null);
        when(adminService.addProduct(any())).thenReturn(1);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(null))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Product nullP = new Product();
        assertEquals(nullP.toString(), new Gson().fromJson(result.getResponse().getContentAsString(), Product.class).toString());
    }
}
