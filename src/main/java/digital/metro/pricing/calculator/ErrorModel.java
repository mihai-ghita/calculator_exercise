package digital.metro.pricing.calculator;

import java.util.List;

public class ErrorModel {

    private List<String> errors;

    public ErrorModel(String error) {
        this.errors = List.of(error);
    }

    public ErrorModel(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
