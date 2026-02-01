package digital.metro.pricing.calculator.controller;

import digital.metro.pricing.calculator.controller.dto.BasketCalculationResponse;
import digital.metro.pricing.calculator.controller.dto.BasketRequest;
import digital.metro.pricing.calculator.domain.Basket;
import digital.metro.pricing.calculator.domain.BasketCalculationResult;
import digital.metro.pricing.calculator.domain.BasketEntry;
import digital.metro.pricing.calculator.mapper.BasketMapper;
import digital.metro.pricing.calculator.service.BasketCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/calculator")
@Tag(name = "Basket Calculator", description = "APIs for calculating basket and article prices")
public class BasketCalculatorController {

    private final BasketCalculatorService basketCalculatorService;
    private final BasketMapper basketMapper;

    @PostMapping("/calculate-basket")
    @Operation(summary = "Calculate basket total", description = "Calculates the total price for all items in the basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Basket calculated successfully",
                    content = @Content(schema = @Schema(implementation = BasketCalculationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid basket data",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<BasketCalculationResponse> calculateBasket(@Valid @RequestBody BasketRequest basketRequest) {
        log.info("Received request to calculate basket for customer: {}", basketRequest.getCustomerId());

        Basket basket = basketMapper.toBasket(basketRequest);
        BasketCalculationResult result = basketCalculatorService.calculateBasket(basket);
        BasketCalculationResponse response = basketMapper.toBasketCalculationResponse(result);

        log.info("Basket calculation completed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Get article price", description = "Retrieves the standard price for a specific article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article price retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<BigDecimal> getArticlePrice(@PathVariable @NotBlank String articleId) {
        log.info("Received request to get price for article: {}", articleId);

        BigDecimal price = basketCalculatorService.calculateArticle(
                new BasketEntry(articleId, BigDecimal.ONE), null);

        log.info("Article price retrieved: {}", price);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/article-price-for-customer")
    @Operation(summary = "Get article price for customer",
            description = "Retrieves the price for a specific article and customer, applying customer-specific discounts if available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article price for customer retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<BigDecimal> getArticlePriceForCustomer(
            @RequestParam @NotBlank String articleId,
            @RequestParam @NotBlank String customerId) {
        log.info("Received request to get price for article: {} and customer: {}", articleId, customerId);

        BigDecimal price = basketCalculatorService.calculateArticle(
                new BasketEntry(articleId, BigDecimal.ONE), customerId);

        log.info("Article price for customer retrieved: {}", price);
        return ResponseEntity.ok(price);
    }
}
