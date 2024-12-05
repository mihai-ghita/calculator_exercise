package digital.metro.pricing.calculator.services;

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

    public BasketCalculationResult calculateBasket(final Basket basket) {
        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticle(entry, basket.getCustomerId())
                                .multiply(entry.getQuantity())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    public BigDecimal calculateArticle(final BasketEntry basketEntry, final String customerId) {
        String articleId = basketEntry.getArticleId();
        BigDecimal customerPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
        if (customerPrice != null) {
            return customerPrice;
        }
        return priceRepository.getPriceByArticleId(articleId);
    }

    public BigDecimal getArticleStandardPrice(final String articleId) {
        return priceRepository.getPriceByArticleId(articleId);
    }

    public BigDecimal getArticleCustomPriceForCustomer(final String articleId, final String customerId) {
        return priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
    }
    
}
