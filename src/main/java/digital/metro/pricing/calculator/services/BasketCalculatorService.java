package digital.metro.pricing.calculator.services;

import digital.metro.pricing.calculator.exceptions.PriceNotFoundException;
import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.models.BasketEntry;
import digital.metro.pricing.calculator.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BasketCalculatorService {

    private final PriceRepository priceRepository;

    @Autowired
    public BasketCalculatorService(final PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public BasketCalculationResult calculateBasketTotalPrice(final Basket basket) {
        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticlePrice(entry.getArticleId(), entry.getQuantity(), basket.getCustomerId())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    private BigDecimal calculateArticlePrice(final String articleId, final BigDecimal quantity,  final String customerId) {
        return priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)
                .or(() -> priceRepository.getPriceByArticleId(articleId))
                .map(price -> price.multiply(quantity))
                .orElseThrow(() -> new PriceNotFoundException(String.format("Price not found for article %s", articleId)));
    }

    public BigDecimal getArticleStandardPrice(final String articleId) {
        return priceRepository.getPriceByArticleId(articleId)
                .orElseThrow(() -> new PriceNotFoundException(String.format("Price not found for article %s", articleId)));
    }

    public BigDecimal getArticlePriceForCustomer(final String articleId, final String customerId) {
        return priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)
                .or(() -> priceRepository.getPriceByArticleId(articleId))
                .orElseThrow(() -> new PriceNotFoundException(String.format("Price not found for article %s", articleId)));
    }
    
}
