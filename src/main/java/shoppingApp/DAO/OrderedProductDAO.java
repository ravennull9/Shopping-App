package shoppingApp.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shoppingApp.domain.Order;
import shoppingApp.domain.Ordered_Product;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderedProductDAO {
    @Autowired
    SessionFactory sessionFactory;

    public List<Ordered_Product> getOrderedProductByOrderId(Integer id){
        Session session;
        List<Ordered_Product> products = null;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ordered_Product> cq = cb.createQuery(Ordered_Product.class);
        Root<Ordered_Product> root = cq.from(Ordered_Product.class);
        Predicate predicate = cb.equal(root.get("order"), id);
        cq.select(root).where(predicate);
        products = session.createQuery(cq).getResultList();
        return products;
    }

    public List<Ordered_Product> getTop3OrderedProductByOrderIds(List<Integer> ids){
        Session session;
        List<Ordered_Product> products = new ArrayList<>();
        session = sessionFactory.getCurrentSession();
        String hql = "SELECT OP.productId, sum(OP.quantity) FROM Ordered_Product OP "+
                "GROUP BY OP.productId HAVING OP.order.id in (:ids) ORDER BY sum(OP.quantity) DESC";
        products = session.createQuery(hql).setParameterList("ids", ids).setMaxResults(3).getResultList();
        return products;
    }


}
