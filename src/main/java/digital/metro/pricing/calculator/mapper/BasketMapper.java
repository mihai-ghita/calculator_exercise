package digital.metro.pricing.calculator.mapper;

import digital.metro.pricing.calculator.controller.dto.BasketCalculationResponse;
import digital.metro.pricing.calculator.controller.dto.BasketEntryDto;
import digital.metro.pricing.calculator.controller.dto.BasketRequest;
import digital.metro.pricing.calculator.domain.Basket;
import digital.metro.pricing.calculator.domain.BasketCalculationResult;
import digital.metro.pricing.calculator.domain.BasketEntry;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BasketMapper {

    Basket toBasket(BasketRequest basketRequest);

    BasketEntry toBasketEntry(BasketEntryDto basketEntryDto);

    BasketCalculationResponse toBasketCalculationResponse(BasketCalculationResult result);
}
