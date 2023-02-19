package shoppingApp.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("shoppingApp.AOP.PointCuts.inProductDAOLayer()")
    public Object logStartAndEndTimeProductDAO(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        // before
        logger.info("From LoggingAspect.logStartAndEndTime invoked by placing a new order: " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        logger.info("Start time: " + start);

        //Invoke the actual object
        Object result = proceedingJoinPoint.proceed();

        // after
        long end = System.currentTimeMillis();
        logger.info("End time: " + end);
        logger.info("Total duration for this operation is: " + (end-start) + "ms");
        return result;
    }

    @Around("shoppingApp.AOP.PointCuts.inOrderDAOLayer()")
    public Object logStartAndEndTimeOrderDAO(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        // before
        logger.info("From LoggingAspect.logStartAndEndTime invoked by updating order status: " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        logger.info("Start time: " + start);

        //Invoke the actual object
        Object result = proceedingJoinPoint.proceed();

        // after
        long end = System.currentTimeMillis();
        logger.info("End time: " + end);
        logger.info("Total duration for this operation is: " + (end-start) + "ms");
        return result;
    }
}
