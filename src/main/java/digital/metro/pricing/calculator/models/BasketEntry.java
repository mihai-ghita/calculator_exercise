package digital.metro.pricing.calculator.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class BasketEntry {

    @NotBlank(message = "ArticleId is null or empty")
    private final String articleId;

    @NotNull(message = "Quantity is null")
    private final BigDecimal quantity;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketEntry that = (BasketEntry) o;
        return Objects.equals(articleId, that.articleId) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, quantity);
    }
}
