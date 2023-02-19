package shoppingApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoppingApp.DAO.ProductDAO;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private ProductDAO productDAO;

    @Autowired
    public ProductService(ProductDAO productDAO){ this.productDAO = productDAO;}

    public List<Product> getAllProducts(){
        return productDAO.getAllProducts();
    }

    public List<Product> getAvailableProducts(){ return productDAO.getAvailableProducts(); }

    public Product getProductById(Integer id){
        return productDAO.getProductById(id);
    }

    public Product getAvailableProductById(Integer id){ return productDAO.getAvailableProductById(id); }

    public void updateProduct(Product product){
        productDAO.updateProduct(product);
    }

    public void restockProductById(int id, int quantity){
        Product p = productDAO.getProductById(id);
        p.setQuantity(p.getQuantity()+quantity);
        productDAO.updateProduct(p);
    }

    public void changeWholeSalePriceById(int id, float price){
        Product p = productDAO.getProductById(id);
        p.setWholesale_price(price);
        productDAO.updateProduct(p);
    }

    public void changeRetailPriceById(int id, float price){
        Product p = productDAO.getProductById(id);
        p.setRetail_price(price);
        productDAO.updateProduct(p);
    }

    public Boolean buyProduct(Product product, Integer quantity){
        return productDAO.buyProduct(product, quantity);
    }


}
