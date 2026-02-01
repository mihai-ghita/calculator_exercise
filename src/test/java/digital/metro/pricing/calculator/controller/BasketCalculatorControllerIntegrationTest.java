package digital.metro.pricing.calculator.controller;

import digital.metro.pricing.calculator.controller.dto.BasketCalculationResponse;
import digital.metro.pricing.calculator.controller.dto.BasketEntryDto;
import digital.metro.pricing.calculator.controller.dto.BasketRequest;
import digital.metro.pricing.calculator.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BasketCalculatorControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private PriceRepository priceRepository;

    @Test
    void shouldCalculateBasketWhenValidRequestProvided() {
        // GIVEN
        BasketRequest request = new BasketRequest("customer-1", Set.of(
                new BasketEntryDto("article-1", BigDecimal.ONE),
                new BasketEntryDto("article-2", new BigDecimal("2"))
        ));

        when(priceRepository.getPriceByArticleId("article-1")).thenReturn(new BigDecimal("10.00"));
        when(priceRepository.getPriceByArticleId("article-2")).thenReturn(new BigDecimal("5.00"));

        // WHEN
        ResponseEntity<BasketCalculationResponse> response = restTemplate.postForEntity(
                "/api/v1/calculator/calculate-basket", request, BasketCalculationResponse.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("customer-1", response.getBody().getCustomerId());
        assertEquals(new BigDecimal("20.00"), response.getBody().getTotalAmount());
    }

    @Test
    void shouldReturnBadRequestWhenBasketEntriesAreEmpty() {
        // GIVEN
        BasketRequest request = new BasketRequest("customer-1", Set.of());

        // WHEN
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/calculator/calculate-basket", request, String.class);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldReturnArticlePriceWhenValidArticleIdProvided() {
        // GIVEN
        String articleId = "article-1";
        when(priceRepository.getPriceByArticleId(articleId)).thenReturn(new BigDecimal("15.99"));

        // WHEN
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "/api/v1/calculator/article/" + articleId, BigDecimal.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("15.99"), response.getBody());
    }

    @Test
    void shouldReturnCustomerSpecificPriceWhenCustomerIdProvided() {
        // GIVEN
        String articleId = "article-1";
        String customerId = "customer-1";
        when(priceRepository.getPriceByArticleId(articleId)).thenReturn(new BigDecimal("100.00"));
        when(priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId))
                .thenReturn(new BigDecimal("90.00"));

        // WHEN
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "/api/v1/calculator/article-price-for-customer?articleId=" + articleId + "&customerId=" + customerId,
                BigDecimal.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("90.00"), response.getBody());
    }
}
