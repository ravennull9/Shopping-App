package shoppingApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoppingApp.DAO.ProductDAO;
import shoppingApp.DAO.WatchedProductDAO;
import shoppingApp.domain.Product;
import shoppingApp.domain.Watched_Product;
import shoppingApp.domain.wrapper.ProductViewWrapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WatchListService {
    private WatchedProductDAO watchedProductDAO;
    private ProductDAO productDAO;

    @Autowired
    public WatchListService(WatchedProductDAO watchedProductDAO, ProductDAO productDAO){
        this.watchedProductDAO = watchedProductDAO;
        this.productDAO = productDAO;
    }

    public List<Watched_Product> getWatchedProductsByUid(int uid){
        return watchedProductDAO.getWatchedProductsByUid(uid);
    }

    public Optional<Watched_Product> getWatchedProductsByUidPid(int uid, int pid){
        return watchedProductDAO.getWatchedProductsByUidPid(uid, pid);
    }

    public List<ProductViewWrapper> getProductViewWrappers(List<Watched_Product> watched_productList){
        List<ProductViewWrapper> productWrapperList = new ArrayList<>();
        for(Watched_Product wp: watched_productList){
            Product p = productDAO.getProductById(wp.getProduct_id());
            if(p.getQuantity() > 0){
                ProductViewWrapper pw = new ProductViewWrapper(p.getId(), p.getRetail_price(), p.getProduct_name());
                productWrapperList.add(pw);
            }
        }
        return productWrapperList;
    }

    public Optional<Watched_Product> getWatchedProductByProductId(int pid, int uid){
        return watchedProductDAO.getWatchedProductByProductId(pid, uid);
    }

    public void deleteWatchedProductByUidPid(int uid, int pid){
        watchedProductDAO.deleteWatchedProductByUidPid(uid, pid);
    }

}
