package digital.metro.pricing.calculator.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BasketEntry {

    @NotBlank(message = "ArticleId is null or empty")
    private String articleId;

    @NotNull(message = "Quantity is null")
    private BigDecimal quantity;

    public BasketEntry(final String articleId, final BigDecimal quantity) {
        this.articleId = articleId;
        this.quantity = quantity;
    }

    public String getArticleId() {
        return articleId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
