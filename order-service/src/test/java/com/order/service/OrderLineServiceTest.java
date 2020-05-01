package com.order.service;

import com.order.dao.OrderLineDao;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.OrderLine;
import com.order.util.converter.OrderLineConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrderLine;
import static com.order.TestDataFactory.buildOrderLineDto;
import static com.order.TestDataFactory.buildPizzaDto;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderLineServiceTest {

    @Mock
    private OrderLineDao mockOrderLineDao;

    @Mock
    private OrderLineConverter mockOrderLineConverter;

    private OrderLineService service;


    @BeforeEach
    public void init() {
        service = new OrderLineService(mockOrderLineDao, mockOrderLineConverter);
    }


    static Stream<Arguments> saveAllTestCases() {
        PizzaDto pizzaDto1 = buildPizzaDto((short)11, "Carbonara", 11.12D);
        PizzaDto pizzaDto2 = buildPizzaDto((short)12, "Margherita", 9.05D);
        OrderLineDto dto1 = buildOrderLineDto(1, 1, pizzaDto1, (short)5, 55.60D);
        OrderLineDto dto2 = buildOrderLineDto(2, 1, pizzaDto2, (short)2, 18.10D);
        OrderLine model1 = buildOrderLine(dto1.getId(), dto1.getOrderId(), pizzaDto1.getId(), dto1.getAmount(), dto1.getCost());
        OrderLine model2 = buildOrderLine(dto2.getId(), dto2.getOrderId(), pizzaDto2.getId(), dto2.getAmount(), dto2.getCost());
        return Stream.of(
                //@formatter:off
                //            dtosToSave,           orderId,   converterToModelResult,   repositoryResult,         converterToDtoResult,   expectedResult
                Arguments.of( null,                 null,      asList(),                 asList(),                 asList(),               asList() ),
                Arguments.of( asList(dto1, dto2),   1,         asList(),                 asList(),                 asList(),               asList() ),
                Arguments.of( asList(dto1, dto2),   1,         asList(model1, model2),   asList(),                 asList(),               asList() ),
                Arguments.of( asList(dto1, dto2),   1,         asList(model1, model2),   asList(model1, model2),   asList(dto1, dto2),     asList(dto1, dto2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveAllTestCases")
    @DisplayName("saveAll: test cases")
    public void saveAll_testCases(List<OrderLineDto> dtosToSave, Integer orderId, List<OrderLine> converterToModelResult,
                                  List<OrderLine> repositoryResult, List<OrderLineDto> converterToDtoResult, List<OrderLineDto> expectedResult) {
        when(mockOrderLineConverter.fromDtosToModels(dtosToSave, orderId)).thenReturn(converterToModelResult);
        when(mockOrderLineConverter.fromModelsToDtos(repositoryResult)).thenReturn(converterToDtoResult);
        when(mockOrderLineDao.saveAll(converterToModelResult)).thenReturn(repositoryResult);

        List<OrderLineDto> result = service.saveAll(dtosToSave, orderId);

        assertEquals(expectedResult, result);
    }

}
