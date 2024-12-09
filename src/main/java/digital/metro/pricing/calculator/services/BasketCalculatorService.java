package digital.metro.pricing.calculator.services;

import digital.metro.pricing.calculator.exceptions.PriceNotFoundException;
import digital.metro.pricing.calculator.models.Basket;
import digital.metro.pricing.calculator.models.BasketCalculationResult;
import digital.metro.pricing.calculator.models.BasketEntry;
import digital.metro.pricing.calculator.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BasketCalculatorService {

    private final PriceRepository priceRepository;

    @Autowired
    public BasketCalculatorService(final PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Mono<BasketCalculationResult> calculateBasketTotalPrice(final Basket basket) {
        return Flux.fromIterable(basket.getEntries())
                .flatMap(entry ->
                        calculateArticlePrice(entry.getArticleId(), entry.getQuantity(), basket.getCustomerId())
                            .map(amount -> new AbstractMap.SimpleEntry<>(entry.getArticleId(), amount)))
                .collectList()
                .map( list -> {
                    BigDecimal totalAmount = list.stream()
                            .map(AbstractMap.SimpleEntry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, BigDecimal> pricedBasketEntries = list.stream().collect(Collectors.toMap(
                            AbstractMap.SimpleEntry::getKey,
                            AbstractMap.SimpleEntry::getValue));
                    return new BasketCalculationResult(basket.getCustomerId(), pricedBasketEntries, totalAmount);
                });
    }

    private Mono<BigDecimal> calculateArticlePrice(final String articleId, final BigDecimal quantity,  final String customerId) {
        return priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)
                .switchIfEmpty(priceRepository.getPriceByArticleId(articleId))
                .map(price -> price.multiply(quantity))
                .switchIfEmpty(
                        Mono.error(new PriceNotFoundException(String.format("Price not found for article %s", articleId)))
                );
    }

    public Mono<BigDecimal> getArticleStandardPrice(final String articleId) {
        return priceRepository.getPriceByArticleId(articleId)
                .switchIfEmpty(
                        Mono.error(new PriceNotFoundException(String.format("Price not found for article %s", articleId)))
                );
    }

    public Mono<BigDecimal> getArticlePriceForCustomer(final String articleId, final String customerId) {
        return priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)
                .switchIfEmpty(priceRepository.getPriceByArticleId(articleId))
                .switchIfEmpty(
                        Mono.error(new PriceNotFoundException(String.format("Price not found for article %s", articleId)))
                );
    }
    
}
