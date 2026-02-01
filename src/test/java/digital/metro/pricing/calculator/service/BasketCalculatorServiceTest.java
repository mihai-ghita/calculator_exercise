package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.domain.Basket;
import digital.metro.pricing.calculator.domain.BasketCalculationResult;
import digital.metro.pricing.calculator.domain.BasketEntry;
import digital.metro.pricing.calculator.exception.InvalidBasketException;
import digital.metro.pricing.calculator.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketCalculatorServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private BasketCalculatorService service;

    @Test
    void shouldCalculateArticlePriceWhenNoCustomerProvided() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal price = new BigDecimal("34.29");
        when(priceRepository.getPriceByArticleId(articleId)).thenReturn(price);

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), null);

        // THEN
        assertEquals(price, result);
    }

    @Test
    void shouldCalculateArticlePriceWithQuantityWhenNoCustomerProvided() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        BigDecimal quantity = new BigDecimal("3");
        when(priceRepository.getPriceByArticleId(articleId)).thenReturn(unitPrice);

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, quantity), null);

        // THEN
        assertEquals(new BigDecimal("30.00"), result);
    }

    @Test
    void shouldApplyCustomerPriceWhenCustomerSpecificPriceExists() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal customerPrice = new BigDecimal("29.99");
        String customerId = "customer-1";

        when(priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)).thenReturn(customerPrice);

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), customerId);

        // THEN
        assertEquals(customerPrice, result);
    }

    @Test
    void shouldUseStandardPriceWhenCustomerSpecificPriceNotAvailable() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        String customerId = "customer-3";

        when(priceRepository.getPriceByArticleId(articleId)).thenReturn(standardPrice);
        when(priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)).thenReturn(null);

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), customerId);

        // THEN
        assertEquals(standardPrice, result);
    }

    @Test
    void shouldCalculateBasketTotalWhenValidBasketProvided() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        when(priceRepository.getPriceByArticleId("article-1")).thenReturn(prices.get("article-1"));
        when(priceRepository.getPriceByArticleId("article-2")).thenReturn(prices.get("article-2"));
        when(priceRepository.getPriceByArticleId("article-3")).thenReturn(prices.get("article-3"));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertEquals("customer-1", result.getCustomerId());
        assertEquals(prices, result.getPricedBasketEntries());
        assertEquals(new BigDecimal("11.78"), result.getTotalAmount());
    }

    @Test
    void shouldThrowInvalidBasketExceptionWhenBasketEntriesAreEmpty() {
        // GIVEN
        Basket basket = new Basket("customer-1", Collections.emptySet());

        // WHEN & THEN
        assertThrows(InvalidBasketException.class, () -> service.calculateBasket(basket));
    }

    @Test
    void shouldThrowInvalidBasketExceptionWhenBasketEntriesAreNull() {
        // GIVEN
        Basket basket = new Basket("customer-1", null);

        // WHEN & THEN
        assertThrows(InvalidBasketException.class, () -> service.calculateBasket(basket));
    }
}
