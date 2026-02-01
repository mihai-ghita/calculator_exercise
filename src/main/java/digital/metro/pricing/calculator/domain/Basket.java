package digital.metro.pricing.calculator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Basket {
    private String customerId;
    private Set<BasketEntry> entries;
}
