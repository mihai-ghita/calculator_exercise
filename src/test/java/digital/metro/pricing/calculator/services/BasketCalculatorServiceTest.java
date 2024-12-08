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
import java.util.Optional;
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
    public void shouldReturnArticlePrice() {
        String articleId = "article-1";
        BigDecimal expectedPrice = new BigDecimal("34.29");
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId))
                .thenReturn(Optional.of(expectedPrice));

        BigDecimal result = service.getArticleStandardPrice(articleId);

        assertThat(result).isEqualByComparingTo(expectedPrice);
    }

    @Test
    public void shouldThrowPriceNotFoundExceptionWhenArticlePriceNotDefined() {
        String articleId = "article-1";
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getArticleStandardPrice(articleId))
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    public void shouldReturnArticleCustomPriceForAGivenCustomer() {
        String customerId = "customer-1";
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        BigDecimal customerPrice = new BigDecimal("29.99");
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId))
                .thenReturn(Optional.of(standardPrice));
        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId))
                .thenReturn(Optional.of(customerPrice));

        BigDecimal result = service.getArticlePriceForCustomer(articleId, customerId);

        assertThat(result).isEqualByComparingTo(customerPrice);
    }

    @Test
    public void shouldReturnArticleStandardPriceForAGivenCustomerWhenCustomPriceNotDefined() {
        String customerId = "customer-5";
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId))
                .thenReturn(Optional.empty());
        Mockito.when(mockPriceRepository.getPriceByArticleId(articleId))
                .thenReturn(Optional.of(standardPrice));

        BigDecimal result = service.getArticlePriceForCustomer(articleId, customerId);

        assertThat(result).isEqualByComparingTo(standardPrice);
    }

    @Test
    public void shouldThrowPriceNotFoundExceptionWhenPriceNotDefined() {
        String articleId = "article-1";
        String customerId = "customer-1";

        Mockito.when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getArticlePriceForCustomer(articleId, customerId))
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    public void shouldCalculateTheBasketPrice() {
        String customerId = "customer-1";
        Basket basket = new Basket(customerId, Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.valueOf(2)),
                new BasketEntry("article-3", BigDecimal.valueOf(3))));
        Map<String, Optional<BigDecimal>> articleToPrice = Map.of(
                "article-1", Optional.of(new BigDecimal("1.50")),
                "article-2", Optional.of(new BigDecimal("0.29")),
                "article-3", Optional.of(new BigDecimal("9.99")));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-1"))
                .thenReturn(articleToPrice.get("article-1"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-2"))
                .thenReturn(articleToPrice.get("article-2"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-3"))
                .thenReturn(articleToPrice.get("article-3"));

        BasketCalculationResult result = service.calculateBasketTotalPrice(basket);


        Map<String, BigDecimal> articleToPriceBaseOnQuantity = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.58"),
                "article-3", new BigDecimal("29.97"));
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getPricedBasketEntries()).isEqualTo(articleToPriceBaseOnQuantity);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("32.05"));
    }

    @Test
    public void shouldThrowPriceNotFoundExceptionWhenPriceNotDefinedForAGivenArticleInsideTheBasket() {
        String customerId = "customer-1";
        Basket basket = new Basket(customerId, Set.of(
                new BasketEntry("article-1", BigDecimal.valueOf(2L)),
                new BasketEntry("article-2", BigDecimal.valueOf(2L)),
                new BasketEntry("article-3", BigDecimal.valueOf(2L))));

        Map<String, Optional<BigDecimal>> prices = Map.of(
                "article-1", Optional.of(new BigDecimal("1.50")),
                "article-2", Optional.of(new BigDecimal("0.29")),
                "article-3", Optional.empty());

        Mockito.when(mockPriceRepository.getPriceByArticleId("article-1"))
                .thenReturn(prices.get("article-1"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-2"))
                .thenReturn(prices.get("article-2"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-3"))
                .thenReturn(prices.get("article-3"));

        assertThatThrownBy(() -> service.calculateBasketTotalPrice(basket))
                .isInstanceOf(PriceNotFoundException.class);
    }

}
