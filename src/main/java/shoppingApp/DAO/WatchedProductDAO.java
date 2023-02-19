package shoppingApp.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shoppingApp.domain.Ordered_Product;
import shoppingApp.domain.Product;
import shoppingApp.domain.User;
import shoppingApp.domain.Watched_Product;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class WatchedProductDAO {
    @Autowired
    SessionFactory sessionFactory;

    public List<Watched_Product> getWatchedProductsByUid(int uid){
        Session session;
        List<Watched_Product> products = null;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Watched_Product> cq = cb.createQuery(Watched_Product.class);
        Root<Watched_Product> root = cq.from(Watched_Product.class);
        Predicate predicate = cb.equal(root.get("user"), uid);
        cq.select(root).where(predicate);
        products = session.createQuery(cq).getResultList();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return products;
    }

    public Optional<Watched_Product> getWatchedProductsByUidPid(int uid, int pid){
        Session session;
        Optional<Watched_Product> possibleProduct = null;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Watched_Product> cq = cb.createQuery(Watched_Product.class);
        Root<Watched_Product> root = cq.from(Watched_Product.class);
        cq.select(root).where(cb.and(cb.equal(root.get("user"), uid), cb.equal(root.get("product_id"), pid)));
        possibleProduct = session.createQuery(cq).uniqueResultOptional();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return possibleProduct;
    }

    public Optional<Watched_Product> getWatchedProductByProductId(int pid, int uid){
        Optional<Watched_Product> possibleProduct = null;
        Session session;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Watched_Product> cq = cb.createQuery(Watched_Product.class);
        Root<Watched_Product> root = cq.from(Watched_Product.class);
        cq.select(root).where(cb.and(cb.equal(root.get("product_id"), pid), cb.equal(root.get("user"), uid)));
        possibleProduct = session.createQuery(cq).uniqueResultOptional();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return possibleProduct;
    }

    public void deleteWatchedProductByUidPid(int uid, int pid){
        Optional<Watched_Product> possibleProduct = null;
        Session session;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Watched_Product> cq = cb.createQuery(Watched_Product.class);
        Root<Watched_Product> root = cq.from(Watched_Product.class);
        cq.select(root).where(cb.and(cb.equal(root.get("product_id"), pid), cb.equal(root.get("user"), uid)));
        possibleProduct = session.createQuery(cq).uniqueResultOptional();
        if(possibleProduct.isPresent()) session.delete(possibleProduct.get());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
