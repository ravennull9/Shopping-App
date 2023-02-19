package shoppingApp.service;

import javassist.compiler.ast.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import shoppingApp.DAO.OrderDAO;
import shoppingApp.DAO.OrderedProductDAO;
import shoppingApp.DAO.ProductDAO;
import shoppingApp.domain.Order;
import shoppingApp.domain.Ordered_Product;
import shoppingApp.domain.wrapper.ProductIdWrapper;
import shoppingApp.domain.wrapper.ProductProfitWrapper;
import shoppingApp.domain.wrapper.ProductQuantityWrapper;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OrderService {

    private OrderDAO orderDAO;
    private OrderedProductDAO orderedProductDAO;
    private ProductDAO productDAO;

    @Autowired
    public OrderService(OrderDAO orderDAO, OrderedProductDAO orderedProductDAO, ProductDAO productDAO){
        this.orderDAO = orderDAO;
        this.orderedProductDAO = orderedProductDAO;
        this.productDAO = productDAO;
    }

    public List<Order> getAllOrders(){
        return orderDAO.getAllOrders();
    }

    public Order getOrderById(Integer id){
        return orderDAO.getOrderById(id);
    }

    public List<Order> getAllOrdersWithStatus(String status, Integer uid){ return orderDAO.getAllOrdersWithStatus(status, uid); }

    public ProductProfitWrapper getTopProfitProduct(){
        List<Order> activeOrders = orderDAO.getAllOrdersWithStatus("Complete");
        Map<Integer, Float> profitMap = new HashMap<>();
        for(Order o: activeOrders){
            List<Ordered_Product> ops = orderedProductDAO.getOrderedProductByOrderId(o.getId());
            for(Ordered_Product op: ops){
                profitMap.put(op.getProductId(), profitMap.getOrDefault(op.getProductId(), (float)0)
                        + op.getQuantity()*(op.getExecute_retail_price()-op.getExecute_wholesale_price()));
            }
        }
        List<Float> values = new ArrayList<>(profitMap.values());
        Collections.sort(values);
        ProductProfitWrapper ppw = new ProductProfitWrapper();
        for(Integer id: profitMap.keySet()){
            if(profitMap.get(id)== values.get(values.size()-1)){
                ppw.setProduct_id(id);
                ppw.setProfit(profitMap.get(id));
            }
        }
//        System.out.println(ppw);
        return ppw;
    }

    //only counts for complete orders and determine based on the sum of all profits from history
    public List<ProductQuantityWrapper> getProductSales(){
        List<Order> activeOrders = orderDAO.getAllOrdersWithStatus("Complete");
        Map<Integer, Integer> map = new HashMap<>();
        List<ProductQuantityWrapper> res = new ArrayList<>();
        for(Order order: activeOrders){
            List<Ordered_Product> products = orderedProductDAO.getOrderedProductByOrderId(order.getId());
            for(Ordered_Product op: products){
                map.put(op.getProductId(), map.getOrDefault(op.getProductId(), 0) + op.getQuantity());
            }
        }
        for(Integer product_id: map.keySet()){
            ProductQuantityWrapper pqw = new ProductQuantityWrapper();
            pqw.setProduct_id(product_id);
            pqw.setQuantity(map.get(product_id));
            res.add(pqw);
        }
        Collections.sort(res, (a,b)->b.getQuantity()-a.getQuantity());
        return res;
    }

    public Map<Integer, Float> getTop3Buyers(){
        List<Order> activeOrders = orderDAO.getAllOrdersWithStatus("Complete");
        Map<Integer, Float> map = new HashMap<>();
        List<ProductQuantityWrapper> res = new ArrayList<>();
        for(Order order: activeOrders){
            map.put(order.getUser().getId(), map.getOrDefault(order.getUser().getId(), (float)0) + order.getSubtotal());
        }
        List<Float> subtotals = new ArrayList<>(map.values());
        Collections.sort(subtotals);
        List<Float> top3Sub = new ArrayList<>();
        for(int i = subtotals.size() - 1; i >= subtotals.size() - 3 && i >= 0; i--){
            top3Sub.add(subtotals.get(i));
        }
        Map<Integer, Float> top3 = new HashMap<>();
        for(int userId: map.keySet()){
            if(top3Sub.contains(map.get(userId))){
                top3.put(userId, map.get(userId));
            }
            if(top3.size()==3) break;
        }
        return top3;
    }

    public List<ProductQuantityWrapper> getTop3Popular(){
        List<Order> activeOrders = orderDAO.getAllOrdersWithStatus("Complete");
        Map<Integer, Integer> map = new HashMap<>();
        List<ProductQuantityWrapper> res = new ArrayList<>();
        for(Order order: activeOrders){
            List<Ordered_Product> products = orderedProductDAO.getOrderedProductByOrderId(order.getId());
            for(Ordered_Product op: products){
                map.put(op.getProductId(), map.getOrDefault(op.getProductId(), 0) + op.getQuantity());
            }
        }
        List<Integer> values = new ArrayList<>(map.values());
        Collections.sort(values, (a,b)->b-a);
        List<Integer> counts = new ArrayList<>();
        for(int value: values){
            counts.add(value);
            if(counts.size() == 3) break;
        }
        for(Integer product_id: map.keySet()){
            if(counts.contains(map.get(product_id))) {
                ProductQuantityWrapper pqw = new ProductQuantityWrapper();
                pqw.setProduct_id(product_id);
                pqw.setQuantity(map.get(product_id));
                res.add(pqw);
            }
            if(res.size() == 3) break;
        }
        Collections.sort(res, (a,b)->b.getQuantity()-a.getQuantity());
        return res;
    }

    public List<ProductQuantityWrapper> topThreeFrequent(Integer uid){
        List<Order> orders = orderDAO.getProcessedOrdersByUId(uid);
//        List<Integer> orderIds = new ArrayList<>();
//        for(Order order: orders) orderIds.add(order.getId());
//        return orderedProductDAO.getTop3OrderedProductByOrderIds(orderIds);
        Map<Integer, Integer> map = new TreeMap<>();
        List<ProductQuantityWrapper> res = new ArrayList<>();
        for(Order order: orders){
            List<Ordered_Product> products = orderedProductDAO.getOrderedProductByOrderId(order.getId());
            for(Ordered_Product op: products){
                map.put(op.getProductId(), map.getOrDefault(op.getProductId(), 0) + op.getQuantity());
            }
        }
        List<Integer> values = new ArrayList<>(map.values());
        Collections.sort(values, (a,b)->b-a);
        List<Integer> counts = new ArrayList<>();
        for(int value: values){
            counts.add(value);
            if(counts.size() == 3) break;
        }
        for(Integer product_id: map.keySet()){
            if(counts.contains(map.get(product_id))) {
                ProductQuantityWrapper pqw = new ProductQuantityWrapper();
                pqw.setProduct_id(product_id);
                pqw.setQuantity(map.get(product_id));
                res.add(pqw);
            }
            if(res.size() == 3) break;
        }
        Collections.sort(res, (a,b)->b.getQuantity()-a.getQuantity());
        return res;
    }

    public List<ProductIdWrapper> topThreeRecent(Integer uid){
        List<Order> orders = orderDAO.getProcessedOrdersByUId(uid);
        Set<Integer> ids = new HashSet<>();
        for(Order order: orders){
            List<Ordered_Product> products = orderedProductDAO.getOrderedProductByOrderId(order.getId());
            for(Ordered_Product op: products){
                ids.add(op.getProductId());
                if(ids.size()==3) break;
            }
            if(ids.size()==3) break;
        }
        List<ProductIdWrapper> res = new ArrayList<>();
        for(int id: ids) res.add(new ProductIdWrapper(id));

        return res;
    }

    public List<Order> getOrdersByUId(int uid){
        return orderDAO.getOrdersByUId(uid);
    }

    public void addOrdered_Product(Order order, Ordered_Product ordered_product){
        order.addOrdered_Product(ordered_product);
    }

    public void placeOrder(Order order){
        orderDAO.placeOrder(order);
    }

    public List<Ordered_Product> getOrderedProductByOrderId(int id){
        return orderedProductDAO.getOrderedProductByOrderId(id);
    }

    public Boolean hasOrder(int uid, int orderId){ return orderDAO.hasOrder(uid, orderId); }

    public List<Order> getAllOrdersWithStatus(String status){ return orderDAO.getAllOrdersWithStatus(status); }

    public void changeOrderStatus(Order order, String status){ orderDAO.changeOrderStatus(order, status); }
}
