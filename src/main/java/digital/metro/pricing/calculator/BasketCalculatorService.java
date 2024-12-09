package digital.metro.pricing.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BasketCalculatorService {

    private final PriceRepository priceRepository;

    @Autowired
    public BasketCalculatorService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public BasketCalculationResult calculateBasket(Basket basket) {
        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticlePrice(entry.getArticleId(), basket.getCustomerId(),
                                    entry.getQuantity())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    private BigDecimal calculateArticlePrice(final String articleId, final String customerId,
                                             final BigDecimal quantity) {
        return getArticlePriceForCustomer(articleId, customerId).multiply(quantity);
    }

    public BigDecimal getArticleStandardPrice(final String articleId) {
        BigDecimal price = priceRepository.getPriceByArticleId(articleId);
        if(price == null){
            throw new PriceNotFoundException(String.format("Price not found for article %s", articleId));
        }
        return price;
    }

    public BigDecimal getArticlePriceForCustomer(final String articleId, final String customerId) {
        BigDecimal customPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
        if(customPrice == null){
            return getArticleStandardPrice(articleId);
        }
        return customPrice;
    }

}
