package digital.metro.pricing.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorModel> handlePriceNotFoundException(PriceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorModel(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

}
