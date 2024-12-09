package digital.metro.pricing.calculator.repositories;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * A dummy implementation for testing purposes. In production, we would get real prices from a database.
 */
@Component
public class PriceRepository {

    private static final BigDecimal CUSTOMER_ONE_PRICE_WEIGHT = new BigDecimal("0.90");
    private static final BigDecimal CUSTOMER_TWO_PRICE_WEIGHT = new BigDecimal("0.90");
    private static final String CUSTOMER_ONE = "customer-1";
    private static final String CUSTOMER_TWO = "customer-2";

    private final Map<String, BigDecimal> prices;
    private final Random random;

    public PriceRepository() {
        this.prices = new HashMap<>();
        this.random = new Random();
    }

    public Mono<BigDecimal> getPriceByArticleId(final String articleId) {
        double price = 0.5d + random.nextDouble() * 29.50d;
        return Mono.fromSupplier(() ->
                        prices.computeIfAbsent(articleId, key ->
                                BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP)
                        )
        );
    }

    public Mono<BigDecimal> getPriceByArticleIdAndCustomerId(final String articleId, final String customerId) {
        switch (customerId) {
            case CUSTOMER_ONE:
                return getPriceByArticleId(articleId)
                        .map(standardPrice -> standardPrice.multiply(CUSTOMER_ONE_PRICE_WEIGHT)
                                .setScale(2, RoundingMode.HALF_UP)
                        );
            case CUSTOMER_TWO:
                return getPriceByArticleId(articleId)
                        .map(standardPrice -> standardPrice.multiply(CUSTOMER_TWO_PRICE_WEIGHT)
                                .setScale(2, RoundingMode.HALF_UP)
                        );
        }
        return Mono.empty();
    }
}
