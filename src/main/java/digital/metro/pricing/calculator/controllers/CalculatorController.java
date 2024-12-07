package digital.metro.pricing.calculator.controllers;

import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.services.BasketCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BasketCalculationResult> calculateBasket(@Valid @RequestBody Basket basket) {
        return ResponseEntity.ok(basketCalculatorService.calculateBasketTotalPrice(basket));
    }

    @GetMapping(value = "/article/{articleId}")
    public ResponseEntity<BigDecimal> getArticleDefaultPrice(@PathVariable String articleId) {
        return ResponseEntity.ok(basketCalculatorService.getArticleStandardPrice(articleId));
    }

    @GetMapping("/getarticlepriceforcustomer")
    public ResponseEntity<BigDecimal> getArticleCustomPriceForCustomer(
             @RequestParam @NotEmpty(message = "ArticleId is null or empty") String articleId,
             @RequestParam @NotEmpty(message = "CustomerId is null or empty") String customerId) {
        return ResponseEntity.ok(basketCalculatorService.getArticleCustomPriceForCustomer(articleId, customerId));
    }
}
