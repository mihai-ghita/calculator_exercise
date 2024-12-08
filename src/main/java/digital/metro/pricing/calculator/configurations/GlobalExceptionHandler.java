package digital.metro.pricing.calculator.configurations;

import digital.metro.pricing.calculator.exceptions.PriceNotFoundException;
import digital.metro.pricing.calculator.models.ErrorModel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorModel> handlePriceNotFoundException(PriceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorModel(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorModel> handleUserNotFoundException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorModel errorModel = new ErrorModel(errors);
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorModel> handleUserNotFoundException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        ErrorModel errorModel = new ErrorModel(errors);
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

}
