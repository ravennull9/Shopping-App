package shoppingApp.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shoppingApp.domain.User;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO {
    @Autowired
    SessionFactory sessionFactory;

//    private PasswordEncoder passwordEncoder;

    public List<User> getAllUser(){
        Session session;
        List<User> users = null;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User>cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root);
        users = session.createQuery(cq).getResultList();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return users;
    }

    public Optional<User> getUserByUsername(String username){
        Session session;
        Optional<User> possibleUser = null;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User>cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Predicate predicate = cb.equal(root.get("username"), username);
        cq.select(root).where(predicate);
        possibleUser = session.createQuery(cq).uniqueResultOptional();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return possibleUser;
    }

    public Optional<User> getUserById(int id){
        Session session;
        Optional<User> possibleUser = null;
//        try{
        session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User>cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Predicate predicate = cb.equal(root.get("id"), id);
        cq.select(root).where(predicate);
        possibleUser = session.createQuery(cq).uniqueResultOptional();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return possibleUser;
    }

    public Optional<User> getUserByEmail(String email){
        Session session;
        Optional<User> possibleUser = null;
//        try{
        session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User>cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Predicate predicate = cb.equal(root.get("email"), email);
        cq.select(root).where(predicate);
        possibleUser = session.createQuery(cq).uniqueResultOptional();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return possibleUser;
    }

    public void addUser(User user, String role){
        Session session;
//        try{
        session = sessionFactory.getCurrentSession();
        user.setRole(role);
        session.persist(user);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void updateUser(User user){
        Session session;
//        try{
        session = sessionFactory.getCurrentSession();
        session.update(user);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

}
