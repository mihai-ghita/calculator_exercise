package digital.metro.pricing.calculator.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketRequest {
    private String customerId;

    @NotEmpty(message = "Basket entries cannot be empty")
    @Valid
    private Set<BasketEntryDto> entries;
}
