package ar.uba.fi.ingsoft1.product_example.common.exception;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException() {
        super("Invalid password. It must contain at least 8 characters, one uppercase letter, and one number.");
    }
}
