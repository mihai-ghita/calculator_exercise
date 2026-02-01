package digital.metro.pricing.calculator.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A dummy implementation for testing purposes. In production, we would get real prices from a database.
 */
@Slf4j
@Repository
public class PriceRepository {

    private final Map<String, BigDecimal> prices = new HashMap<>();
    private final Random random = new Random();

    public BigDecimal getPriceByArticleId(String articleId) {
        log.debug("Fetching price for article: {}", articleId);
        return prices.computeIfAbsent(articleId,
                key -> {
                    BigDecimal price = BigDecimal.valueOf(0.5d + random.nextDouble() * 29.50d)
                            .setScale(2, RoundingMode.HALF_UP);
                    log.debug("Generated price {} for article {}", price, articleId);
                    return price;
                });
    }

    public BigDecimal getPriceByArticleIdAndCustomerId(String articleId, String customerId) {
        log.debug("Fetching price for article: {} and customer: {}", articleId, customerId);

        switch(customerId) {
            case "customer-1":
                return getPriceByArticleId(articleId).multiply(new BigDecimal("0.90"))
                        .setScale(2, RoundingMode.HALF_UP);
            case "customer-2":
                return getPriceByArticleId(articleId).multiply(new BigDecimal("0.85"))
                        .setScale(2, RoundingMode.HALF_UP);
        }

        return null;
    }
}
