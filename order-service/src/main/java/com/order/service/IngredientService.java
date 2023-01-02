package com.order.service;

import com.order.dto.IngredientAmountDto;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.grpc.service.IngredientServiceGrpcImpl;
import com.order.model.Order;
import com.spring5microservices.common.util.CollectionUtil;
import com.spring5microservices.grpc.IngredientResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
public class IngredientService {

    @Lazy
    private final IngredientServiceGrpcImpl ingredientServiceGrpc;

    @Lazy
    private final OrderService orderService;


    /**
     * Returns the summary of the ingredients used by the given {@link OrderDto#getId()}.
     *
     * @param orderId
     *    {@link Order#getId()} to find the ingredient's summary
     *
     * @return {@link Optional} of {@link Set} of {@link IngredientAmountDto} is the given {@code orderId} was found,
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Set<IngredientAmountDto>> getSummaryByOrderId(final Integer orderId) {
        return orderService.findByIdWithOrderLines(orderId)
                .map(order ->
                        order.getOrderLines().stream()
                                .map(OrderLineDto::getPizza)
                                .filter(Objects::nonNull)
                                .map(PizzaDto::getId)
                                .collect(toList())
                )
                .map(this::getByPizzaIds);
    }


    private Set<IngredientAmountDto> getByPizzaIds(final Collection<Short> pizzaIds) {
        List<IngredientResponse> ingredientGrpc = pizzaIds.stream()
                .flatMap(id -> ingredientServiceGrpc.getByPizzaId(id).stream())
                .collect(toList());

        Map<String, Integer> ingredientCount = CollectionUtil.groupMapReduce(
                ingredientGrpc,
                IngredientResponse::getName,
                ing -> 1,
                Integer::sum
        );
        Set<IngredientAmountDto> result = new HashSet<>();
        for (var entry : ingredientCount.entrySet()) {
            result.add(
               new IngredientAmountDto(
                       entry.getKey(),
                       entry.getValue()
               )
            );
        }
        return result;
    }

}
