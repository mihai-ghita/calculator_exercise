package digital.metro.pricing.calculator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketEntry {
    private String articleId;
    private BigDecimal quantity;
}
