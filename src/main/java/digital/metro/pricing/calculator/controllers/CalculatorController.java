package digital.metro.pricing.calculator.controllers;

import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.services.BasketCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final BasketCalculatorService basketCalculatorService;

    @Autowired
    public CalculatorController(final BasketCalculatorService basketCalculatorService) {
        this.basketCalculatorService = basketCalculatorService;
    }

    @PostMapping("/calculate-basket")
    public Mono<ResponseEntity<BasketCalculationResult>> calculateBasket(@Valid @RequestBody Basket basket) {
        return basketCalculatorService.calculateBasketTotalPrice(basket).map(ResponseEntity::ok);
    }

    @GetMapping(value = "/article/{articleId}")
    public Mono<ResponseEntity<BigDecimal>> getArticleDefaultPrice(@PathVariable String articleId) {
        return basketCalculatorService.getArticleStandardPrice(articleId).map(ResponseEntity::ok);
    }

    @GetMapping("/getarticlepriceforcustomer")
    public Mono<ResponseEntity<BigDecimal>> getArticleCustomPriceForCustomer(
             @RequestParam @NotEmpty(message = "ArticleId is null or empty") String articleId,
             @RequestParam @NotEmpty(message = "CustomerId is null or empty") String customerId) {
        return basketCalculatorService.getArticlePriceForCustomer(articleId, customerId).map(ResponseEntity::ok);
    }

}
