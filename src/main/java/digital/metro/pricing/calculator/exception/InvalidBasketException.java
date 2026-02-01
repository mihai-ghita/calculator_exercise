package digital.metro.pricing.calculator.exception;

public class InvalidBasketException extends RuntimeException {

    public InvalidBasketException(String message) {
        super(message);
    }

    public InvalidBasketException(String message, Throwable cause) {
        super(message, cause);
    }
}
