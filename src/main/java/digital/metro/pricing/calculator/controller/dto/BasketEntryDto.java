package digital.metro.pricing.calculator.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketEntryDto {
    @NotBlank(message = "Article ID is required")
    private String articleId;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;
}
