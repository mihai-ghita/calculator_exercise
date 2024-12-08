package digital.metro.pricing.calculator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import digital.metro.pricing.calculator.exceptions.PriceNotFoundException;
import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.models.BasketEntry;
import digital.metro.pricing.calculator.services.BasketCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketCalculatorService basketCalculatorService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void shouldReturnArticleStandardPrice() throws Exception {
        String articleId = "article-1";
        BigDecimal price = BigDecimal.ONE;
        when(basketCalculatorService.getArticleStandardPrice(articleId)).thenReturn(price);

        mockMvc.perform(get("/calculator/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().string(price.toString()));
    }

    @Test
    public void shouldReturnErrorModelsWhenArticleStandardPriceNotFound() throws Exception {
        String articleId = "article-1";

        when(basketCalculatorService.getArticleStandardPrice(articleId))
                .thenThrow(new PriceNotFoundException("Price not found"));

        mockMvc.perform(get("/calculator/article/" + articleId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(notNullValue()));;
    }

    @Test
    public void shouldReturnArticleCustomerPriceForGivenCustomer() throws Exception {
        String articleId = "article-1";
        String customerId = "customer-1";
        BigDecimal price = BigDecimal.ONE;
        when(basketCalculatorService.getArticlePriceForCustomer(articleId, customerId)).thenReturn(price);

        mockMvc.perform(get("/calculator/getarticlepriceforcustomer")
                    .queryParam("articleId", articleId)
                    .queryParam("customerId", customerId))
                .andExpect(status().isOk())
                .andExpect(content().string(price.toString()));
    }

    @Test
    public void shouldReturnErrorModelsWhenCustomerIdNotProvided() throws Exception {
        String articleId = "article-1";

        mockMvc.perform(get("/calculator/getarticlepriceforcustomer")
                        .queryParam("articleId", articleId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    public void shouldReturnErrorModelsWhenArticleStandardPriceForCustomerNotFound() throws Exception {
        String articleId = "article-1";
        String customerId = "customer-1";
        when(basketCalculatorService.getArticlePriceForCustomer(articleId, customerId))
                .thenThrow(new PriceNotFoundException("Price not found"));

        mockMvc.perform(get("/calculator/getarticlepriceforcustomer")
                        .queryParam("articleId", articleId)
                        .queryParam("customerId", customerId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    public void shouldReturnBasketCalculationResult() throws Exception {
        String customerId = "customer-1";
        Basket basket = new Basket(customerId,
                Set.of(
                        new BasketEntry("article-1", BigDecimal.ONE),
                        new BasketEntry("article-2", BigDecimal.valueOf(2)),
                        new BasketEntry("article-3", BigDecimal.valueOf(3))
                )
        );
        String jsonBasket = OBJECT_MAPPER.writeValueAsString(basket);
        BasketCalculationResult result = new BasketCalculationResult(customerId,
                Map.of(
                        "article-1", BigDecimal.ONE,
                        "article-2", BigDecimal.valueOf(2),
                        "article-3", BigDecimal.valueOf(3)
                ),
                new BigDecimal("32.05")
        );

        when(basketCalculatorService.calculateBasketTotalPrice(basket)).thenReturn(result);

        mockMvc.perform(post("/calculator/calculate-basket")
                    .content(jsonBasket)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    public void shouldReturnErrorModelThenCustomerIdIsNull() throws Exception {
        String customerId = null;
        Basket basket = new Basket(customerId, Set.of(new BasketEntry("article-1", BigDecimal.ONE)));
        String jsonBasket = OBJECT_MAPPER.writeValueAsString(basket);

        mockMvc.perform(post("/calculator/calculate-basket")
                        .content(jsonBasket)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    public void shouldReturnErrorModelThenTheBasketContainerNullQuantityValueForAnArticle() throws Exception {
        String customerId = "customer-1";
        Basket basket = new Basket(customerId, Set.of(new BasketEntry("article-1", null)));
        String jsonBasket = OBJECT_MAPPER.writeValueAsString(basket);

        mockMvc.perform(post("/calculator/calculate-basket")
                        .content(jsonBasket)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(notNullValue()));
    }

}
