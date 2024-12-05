package digital.metro.pricing.calculator.controllers;

import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.services.BasketCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/calculator")
public class CalculatorResource {

    private final BasketCalculatorService basketCalculatorService;

    @Autowired
    public CalculatorResource(final BasketCalculatorService basketCalculatorService) {
        this.basketCalculatorService = basketCalculatorService;
    }

    @PostMapping("/calculate-basket")
    public BasketCalculationResult calculateBasket(@RequestBody Basket basket) {
        return basketCalculatorService.calculateBasket(basket);
    }

    @GetMapping("/article/{articleId}")
    public BigDecimal getArticleDefaultPrice(@PathVariable String articleId) {
        return basketCalculatorService.getArticleStandardPrice(articleId);
    }

    @GetMapping("/getarticlepriceforcustomer")
    public BigDecimal getArticleCustomPriceForCustomer(@RequestParam String articleId, @RequestParam String customerId) {
        return basketCalculatorService.getArticleCustomPriceForCustomer(articleId, customerId);
    }
}
