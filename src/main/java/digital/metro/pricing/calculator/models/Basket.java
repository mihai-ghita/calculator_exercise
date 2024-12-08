package digital.metro.pricing.calculator.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

public class Basket {

    @NotNull(message = "CustomerId is null")
    private String customerId;

    @Valid
    @NotNull(message = "The basked entries is null")
    private Set<BasketEntry> entries;

    public Basket(final String customerId, final Set<BasketEntry> entries) {
        this.customerId = customerId;
        this.entries = entries;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Set<BasketEntry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return Objects.equals(customerId, basket.customerId) && this.entries.containsAll(((Basket) o).entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, entries);
    }

}
