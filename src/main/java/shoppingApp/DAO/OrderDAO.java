package shoppingApp.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shoppingApp.domain.Order;
import shoppingApp.domain.Ordered_Product;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAO {
    @Autowired
    SessionFactory sessionFactory;

    public List<Order> getAllOrders(){
        Session session;
        List<Order> orders = null;
        session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Order O ORDER BY O.order_date DESC");
        orders = query.getResultList();

        return orders;
    }

    public List<Order> getAllOrdersWithStatus(String status){
        Session session;
        List<Order> orders = null;
        session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Order O WHERE O.status='" + status+"'");
        orders = query.getResultList();

        return orders;
    }

    public List<Order> getAllOrdersWithStatus(String status, Integer uid){
        Session session;
        List<Order> orders = null;
        session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Order O WHERE O.status='" + status+"' AND O.user="+uid);
        orders = query.getResultList();

        return orders;
    }

    public Order getOrderById(Integer id){
        Session session;
        Optional<Order> possibleOrder = null;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        Predicate predicate = cb.equal(root.get("id"), id);
        cq.select(root).where(predicate);
        possibleOrder = session.createQuery(cq).uniqueResultOptional();

        return possibleOrder.isPresent() ? possibleOrder.get() : null;
    }

    public List<Order> getOrdersByUId(int uid){
        Session session;
        List<Order> orders = new ArrayList<>();
        session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Order O WHERE O.user="+uid+" ORDER BY O.order_date DESC");
        orders = query.getResultList();
        return orders;
    }

    public List<Order> getProcessedOrdersByUId(int uid){
        Session session;
        List<Order> orders = new ArrayList<>();
        session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Order O WHERE O.user="+uid+" AND NOT O.status='Canceled' ORDER BY O.order_date DESC");
        orders = query.getResultList();
        return orders;
    }

    public Boolean hasOrder(int uid, int orderId){
        Session session;
        Optional<Order> possibleOrder = null;
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        cq.select(root).where(cb.and(cb.equal(root.get("user"), uid), cb.equal(root.get("id"), orderId)));
        possibleOrder = session.createQuery(cq).uniqueResultOptional();
        return possibleOrder.isPresent() ? true : false;
    }

    public void changeOrderStatus(Order order, String status){
        Session session;
        session = sessionFactory.getCurrentSession();
        order.setStatus(status);
        session.update(order);
    }

    public void placeOrder(Order order){
        Session session;
        session = sessionFactory.getCurrentSession();
        session.persist(order);
    }
}
