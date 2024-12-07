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
                        entry -> getArticlePrice(entry.getArticleId(), basket.getCustomerId())
                                .multiply(entry.getQuantity())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    private BigDecimal getArticlePrice(final String articleId, final String customerId) {
        // logica trebuie rescrisa mai frumos folosind optinal
        // de revizuit logica, sa permita customer id null
        BigDecimal customPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
        if (customPrice != null) {
            return customPrice;
        }
        return getArticleStandardPrice(articleId);
    }

    public BigDecimal getArticleStandardPrice(final String articleId) {
        BigDecimal price = priceRepository.getPriceByArticleId(articleId);
        if(price == null){
            throw new PriceNotFoundException("Price not found for article " + articleId);
        }
        return price;
    }

    public BigDecimal getArticleCustomPriceForCustomer(final String articleId, final String customerId) {
        BigDecimal customPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
        if(customPrice == null){
            throw new PriceNotFoundException("Price not found for the article " + articleId + " and the customer " + customerId);
        }
        return customPrice;
    }
    
}
