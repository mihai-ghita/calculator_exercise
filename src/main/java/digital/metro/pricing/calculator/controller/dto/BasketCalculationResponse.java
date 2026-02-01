package digital.metro.pricing.calculator.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketCalculationResponse {
    private String customerId;
    private Map<String, BigDecimal> pricedBasketEntries;
    private BigDecimal totalAmount;
}
