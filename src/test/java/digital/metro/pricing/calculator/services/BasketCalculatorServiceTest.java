package digital.metro.pricing.calculator.services;

import digital.metro.pricing.calculator.exceptions.PriceNotFoundException;
import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.models.BasketEntry;
import digital.metro.pricing.calculator.repositories.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    public void shouldReturnAValidPrice() {
        String articleId = "article-1";
        BigDecimal expectedPrice = new BigDecimal("34.29");
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(expectedPrice);

        BigDecimal actualPrice = service.getArticleStandardPrice(articleId);

        assertThat(actualPrice).isEqualByComparingTo(expectedPrice);
    }

    @Test
    public void shouldReturnAValidCustomPriceForAGivenCustomer() {
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        BigDecimal customerPrice = new BigDecimal("29.99");
        String customerId = "customer-1";

        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(standardPrice);
        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)).thenReturn(customerPrice);

        BigDecimal result = service.getArticleCustomPriceForCustomer(articleId, customerId);

        assertThat(result).isEqualByComparingTo(customerPrice);
    }

    @Test
    public void shouldReturnPriceNotFoundExceptionWhenCustomPriceNotDefined() {
        String articleId = "article-1";
        BigDecimal customerPrice = null;
        String customerId = "customer-1";

        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId))
                .thenReturn(customerPrice);

        assertThatThrownBy(() -> service.getArticleCustomPriceForCustomer(articleId, customerId))
                .as("Price not found for the article " + articleId + " and the customer " + customerId)
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    public void shouldCalculateTheProperPriceWhenTheQuantityIsOneForEachArticle() {
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

        BasketCalculationResult result = service.calculateBasketTotalPrice(basket);

        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getPricedBasketEntries()).isEqualTo(prices);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("11.78"));
    }

    @Test
    public void shouldCalculateTheProperPriceWhenTheQuantityIsTwoForEachArticle() {
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

        BasketCalculationResult result = service.calculateBasketTotalPrice(basket);

        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getPricedBasketEntries()).isEqualTo(pricesPerArticleQuantity);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("23.56"));
    }

}
