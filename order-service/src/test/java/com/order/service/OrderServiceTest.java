package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.util.converter.OrderConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrder;
import static com.order.TestDataFactory.buildOrderDto;
import static com.order.TestDataFactory.buildOrderLineDto;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderService.class)
public class OrderServiceTest {

    @MockBean
    private OrderDao mockOrderDao;

    @MockBean
    private OrderConverter mockOrderConverter;

    @MockBean
    private OrderLineService mockOrderLineService;

    @Autowired
    private OrderService service;


    static Stream<Arguments> findByIdWithOrderLinesTestCases() {
        OrderLineDto lineDto = buildOrderLineDto(11, 1, null, (short)5, 7.50D);
        OrderDto dto = buildOrderDto(1, "Order1", new Date(), asList(lineDto));
        return Stream.of(
                //@formatter:off
                //            id,            repositoryResult,   expectedResult
                Arguments.of( null,          empty(),            empty() ),
                Arguments.of( 1,             empty(),            empty() ),
                Arguments.of( dto.getId(),   of(dto),            of(dto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdWithOrderLinesTestCases")
    @DisplayName("findByIdWithOrderLines: test cases")
    public void findByIdWithOrderLines_testCases(Integer id, Optional<OrderDto> repositoryResult, Optional<OrderDto> expectedResult) {
        when(mockOrderDao.fetchToOrderDtoByIdWithOrderLineDto(id)).thenReturn(repositoryResult);

        Optional<OrderDto> result = service.findByIdWithOrderLines(id);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> findPageOrderedByCreatedWithOrderLinesTestCases() {
        OrderLineDto lineDto1 = buildOrderLineDto(11, 1, null, (short)5, 7.50D);
        OrderLineDto lineDto2 = buildOrderLineDto(12, 2, null, (short)3, 6.75D);
        OrderDto dto1 = buildOrderDto(1, "Order1", new Date(), asList(lineDto1));
        OrderDto dto2 = buildOrderDto(2, "Order2", new Date(), asList(lineDto2));
        return Stream.of(
                //@formatter:off
                //            page,   size,   repositoryResult,     expectedResult
                Arguments.of( 0,      2,      Set.of(),             Set.of() ),
                Arguments.of( 0,      2,      Set.of(dto1, dto2),   Set.of(dto1, dto2) )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findPageOrderedByCreatedWithOrderLinesTestCases")
    @DisplayName("findPageOrderedByCreatedWithOrderLines: test cases")
    public void findPageOrderedByCreatedWithOrderLines_testCases(int page, int size, Set<OrderDto> repositoryResult,
                                                                 Set<OrderDto> expectedResult) {
        when(mockOrderDao.fetchPageToOrderDtoByIdWithOrderLineDto(page, size)).thenReturn(repositoryResult);

        Set<OrderDto> result = service.findPageOrderedByCreatedWithOrderLines(page, size);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> saveTestCases() {
        OrderDto dto = buildOrderDto(null, "Order1", new Date(), asList());
        Order model = buildOrder(dto.getId(), dto.getCode(), new Timestamp(dto.getCreated().getTime()));
        return Stream.of(
                //@formatter:off
                //            orderDto,   converterToModelResult,   repositoryResult,   converterToDtoResult,   expectedResult
                Arguments.of( null,       empty(),                  empty(),            empty(),                empty() ),
                Arguments.of( dto,        empty(),                  empty(),            empty(),                empty() ),
                Arguments.of( dto,        of(model),                empty(),            empty(),                empty() ),
                Arguments.of( dto,        of(model),                of(model),          empty(),                empty() ),
                Arguments.of( dto,        of(model),                of(model),          of(dto),                of(dto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(OrderDto orderDto, Optional<Order> converterToModelResult, Optional<Order> repositoryResult,
                               Optional<OrderDto> converterToDtoResult, Optional<OrderDto> expectedResult) {
        when(mockOrderConverter.fromDtoToOptionalModel(orderDto)).thenReturn(converterToModelResult);
        if (repositoryResult.isPresent()) {
            when(mockOrderConverter.fromModelToOptionalDto(repositoryResult.get())).thenReturn(converterToDtoResult);
        }
        if (converterToModelResult.isPresent()) {
            when(mockOrderDao.save(converterToModelResult.get())).thenReturn(repositoryResult);
        }

        Optional<OrderDto> result = service.save(orderDto);

        assertEquals(expectedResult, result);
        if (converterToModelResult.isPresent()) {
            verify(mockOrderLineService, times(1)).saveAll(orderDto.getOrderLines(),
                    converterToModelResult.get().getId());
        }
    }

}
