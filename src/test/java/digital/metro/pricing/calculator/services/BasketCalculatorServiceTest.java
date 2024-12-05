package digital.metro.pricing.calculator.services;

import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.models.BasketEntry;
import digital.metro.pricing.calculator.repositories.PriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class BasketCalculatorServiceTest {

    @Mock
    private PriceRepository mockPriceRepository;

    private BasketCalculatorService service;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        service = new BasketCalculatorService(mockPriceRepository);
    }

    @Test
    public void testCalculateArticle() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal price = new BigDecimal("34.29");
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(price);

        // WHEN
        BigDecimal result = service.getArticleStandardPrice(articleId);

        // THEN
        Assertions.assertThat(result).isEqualByComparingTo(price);
    }

    @Test
    public void testCalculateArticleForCustomer() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        BigDecimal customerPrice = new BigDecimal("29.99");
        String customerId = "customer-1";

        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(standardPrice);
        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)).thenReturn(customerPrice);

        // WHEN
        BigDecimal result = service.getArticleCustomPriceForCustomer(articleId, customerId);

        // THEN
        Assertions.assertThat(result).isEqualByComparingTo(customerPrice);
    }

    @Test
    public void testCalculateBasket() {
        // GIVEN
        String customerId = "customer-1";
        Basket basket = new Basket(customerId, Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        Mockito.when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(prices.get("article-1"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-2")).thenReturn(prices.get("article-2"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-3")).thenReturn(prices.get("article-3"));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        Assertions.assertThat(result.getCustomerId()).isEqualTo(customerId);
        Assertions.assertThat(result.getPricedBasketEntries()).isEqualTo(prices);
        Assertions.assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("11.78"));
    }

    @Test
    public void testCalculateBasketTwo() {
        // GIVEN
        String customerId = "customer-1";
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.valueOf(2L)),
                new BasketEntry("article-2", BigDecimal.valueOf(2L)),
                new BasketEntry("article-3", BigDecimal.valueOf(2L))));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        Map<String, BigDecimal> pricesPerArticleQuantity = Map.of(
                "article-1", new BigDecimal("3.00"),
                "article-2", new BigDecimal("0.58"),
                "article-3", new BigDecimal("19.98"));

        Mockito.when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(prices.get("article-1"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-2")).thenReturn(prices.get("article-2"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-3")).thenReturn(prices.get("article-3"));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        Assertions.assertThat(result.getCustomerId()).isEqualTo(customerId);
        Assertions.assertThat(result.getPricedBasketEntries()).isEqualTo(pricesPerArticleQuantity);
        Assertions.assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("23.56"));
    }

}
