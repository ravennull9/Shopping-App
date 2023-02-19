package shoppingApp.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shoppingApp.domain.Product;
import shoppingApp.exception.NotEnoughInventoryException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAO {
    @Autowired
    SessionFactory sessionFactory;

    /**
     * Admin feature
     * @param product
     */
    public int addProduct(Product product){
        Session session;
        int product_id = -1;
        session = sessionFactory.getCurrentSession();
        product_id = (int) session.save(product);
        return product_id;
    }

    /**
     * Admin feature
     * @param product
     */
    public void updateProduct(Product product){
        Session session;
        session = sessionFactory.getCurrentSession();
        session.update(product);
    }

    public List<Product> getAllProducts(){
        List<Product> products = null;
        Session session;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root);
        products = session.createQuery(cq).getResultList();

        return products;
    }

    public List<Product> getAvailableProducts(){
        List<Product> products = null;
        Session session;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.greaterThan(root.get("quantity"), 0));
        products = session.createQuery(cq).getResultList();

        return products;
    }

    public Product getProductById(Integer id){
        Optional<Product> possibleProduct = null;
        Session session;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        Predicate predicate = cb.equal(root.get("id"), id);
        cq.select(root).where(predicate);
        possibleProduct = session.createQuery(cq).uniqueResultOptional();
        return possibleProduct.isPresent() ? possibleProduct.get() : null;
    }

    public Product getAvailableProductById(Integer id){
        Optional<Product> possibleProduct = null;
        Session session;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.and(cb.equal(root.get("id"), id), cb.greaterThan(root.get("quantity"), 0)));
        possibleProduct = session.createQuery(cq).uniqueResultOptional();
        return possibleProduct.isPresent() ? possibleProduct.get() : null;
    }

    public Boolean buyProduct(Product product, Integer quantity){
        Session session;
        session = sessionFactory.getCurrentSession();
        if(product==null) {
            return false;
        }
        if(product.getQuantity() < quantity) {
            String msg = "for product " + product.getProduct_name() + ": there is no enough in stock!";
            throw new NotEnoughInventoryException(msg);
        }
        product.setQuantity(product.getQuantity()-quantity);
        session.update(product);
        return true;

    }
}
