package shoppingApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoppingApp.DAO.ProductDAO;
import shoppingApp.domain.Product;

import javax.transaction.Transactional;

@Service
@Transactional
public class AdminService {
    private ProductDAO productDAO;

    @Autowired
    public AdminService(ProductDAO productDAO){ this.productDAO = productDAO;}
    public int addProduct(Product product){
        return productDAO.addProduct(product);
    }
}
