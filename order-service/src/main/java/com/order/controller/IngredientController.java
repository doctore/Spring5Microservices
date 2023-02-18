package com.order.controller;

import com.order.annotation.RoleAdminOrUser;
import com.order.configuration.rest.RestRoutes;
import com.order.dto.IngredientAmountDto;
import com.order.dto.OrderDto;
import com.order.model.Order;
import com.order.service.IngredientService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.Set;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping(RestRoutes.ORDER.ROOT + "/{orderId}")
@Validated
public class IngredientController {

    @Lazy
    private final IngredientService service;


    /**
     * Returns the summary of the ingredients used by the given {@link OrderDto#getId()}.
     *
     * @param orderId
     *    {@link Order#getId()} to find the ingredient's summary
     *
     * @return if {@code orderId} was found: {@link HttpStatus#OK} and {@link Set} of {@link IngredientAmountDto}
     *         if {@code orderId} was not found: {@link HttpStatus#NOT_FOUND}
     */
    @Operation(
            summary = "Return summary of the ingredients used by given order identifier",
            description = "Return summary of the ingredients used by given order identifier (only allowed to user with role admin/user)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is an order with the given orderId",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "Set", implementation = IngredientAmountDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no an order with the given orderId"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping(RestRoutes.INGREDIENT.ROOT + RestRoutes.INGREDIENT.SUMMARY)
    @Transactional(readOnly = true)
    @RoleAdminOrUser
    public ResponseEntity<Set<IngredientAmountDto>> getSummaryByOrderId(@PathVariable @Positive final Integer orderId) {
        log.info(
                format("Getting ingredient's summary of the order with identifier: %d",
                        orderId)
        );
        return service.getSummaryByOrderId(orderId)
                .map(p ->
                        new ResponseEntity<>(
                                p,
                                OK
                        )
                )
                .orElseGet(() ->
                        new ResponseEntity<>(NOT_FOUND)
                );
    }

}
