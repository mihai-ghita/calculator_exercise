package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.domain.Basket;
import digital.metro.pricing.calculator.domain.BasketCalculationResult;
import digital.metro.pricing.calculator.domain.BasketEntry;
import digital.metro.pricing.calculator.exception.InvalidBasketException;
import digital.metro.pricing.calculator.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasketCalculatorService {

    private final PriceRepository priceRepository;

    public BasketCalculationResult calculateBasket(Basket basket) {
        log.info("Calculating basket for customer: {}", basket.getCustomerId());

        if (basket.getEntries() == null || basket.getEntries().isEmpty()) {
            throw new InvalidBasketException("Basket entries cannot be empty");
        }

        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticle(entry, basket.getCustomerId())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Basket calculation completed. Total amount: {}", totalAmount);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    public BigDecimal calculateArticle(BasketEntry basketEntry, String customerId) {
        String articleId = basketEntry.getArticleId();
        BigDecimal quantity = basketEntry.getQuantity();

        log.debug("Calculating price for article: {}, quantity: {}, customer: {}",
                articleId, quantity, customerId);

        BigDecimal unitPrice;

        if (customerId != null) {
            BigDecimal customerPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
            if (customerPrice != null) {
                unitPrice = customerPrice;
                log.debug("Using customer-specific price: {}", unitPrice);
            } else {
                unitPrice = priceRepository.getPriceByArticleId(articleId);
                log.debug("Using standard price: {}", unitPrice);
            }
        } else {
            unitPrice = priceRepository.getPriceByArticleId(articleId);
            log.debug("Using standard price: {}", unitPrice);
        }

        BigDecimal totalPrice = unitPrice.multiply(quantity);
        log.debug("Total price for article {}: {}", articleId, totalPrice);

        return totalPrice;
    }
}
