package shoppingApp.AOP;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shoppingApp.domain.response.ErrorResponse;
import shoppingApp.exception.NotEnoughInventoryException;

import java.io.InvalidClassException;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleInvalidClassException(Exception e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {InvalidClassException.class})
    public ResponseEntity<ErrorResponse> handleInvalidClassException(InvalidClassException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NotEnoughInventoryException.class})
    public ResponseEntity<ErrorResponse> handleNotEnoughInventoryException(NotEnoughInventoryException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }
}
