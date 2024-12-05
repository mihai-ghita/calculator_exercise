package digital.metro.pricing.calculator.models;

import java.math.BigDecimal;
import java.util.Map;

public class BasketCalculationResult {

    private String customerId;
    private Map<String, BigDecimal> pricedBasketEntries;
    private BigDecimal totalAmount;

    private BasketCalculationResult() {}

    public BasketCalculationResult(final String customerId, final Map<String, BigDecimal> pricedBasketEntries,
                                   final BigDecimal totalAmount) {
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
