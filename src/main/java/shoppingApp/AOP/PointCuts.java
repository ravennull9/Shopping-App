package shoppingApp.AOP;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointCuts {
    @Pointcut("within(shoppingApp.controller.*)")
    public void inControllerLayer(){}

    @Pointcut("bean(*Service)")
    public void inService(){}

    @Pointcut("execution(* shoppingApp.DAO.ProductDAO.buyProduct(..))")
    public void inProductDAOLayer(){}

    @Pointcut("execution(* shoppingApp.DAO.OrderDAO.changeOrderStatus(..))")
    public void inOrderDAOLayer(){}
}
