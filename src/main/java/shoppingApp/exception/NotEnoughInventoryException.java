package shoppingApp.exception;

public class NotEnoughInventoryException extends RuntimeException{
    public NotEnoughInventoryException(String msg){
        super(msg);
    }
}
