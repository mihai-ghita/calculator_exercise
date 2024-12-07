package digital.metro.pricing.calculator.models;

import java.math.BigDecimal;
import java.util.Map;

public class BasketCalculationResult {

    private String customerId;
    private Map<String, BigDecimal> pricedBasketEntries;
    private BigDecimal totalAmount;

    private BasketCalculationResult() {
    }
    //de pus final la toti paramentri campurilor
    public BasketCalculationResult(String customerId, Map<String, BigDecimal> pricedBasketEntries, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.pricedBasketEntries = pricedBasketEntries;
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Map<String, BigDecimal> getPricedBasketEntries() {
        return pricedBasketEntries;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
