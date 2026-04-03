package ar.uba.fi.ingsoft1.product_example.common.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String userName){
        super("User " + userName + " already exists");
    }
}
