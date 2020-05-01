package com.order.dao;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@JooqTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class OrderDaoTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    private DSLContext dslContext;

    private OrderDao orderDao;

    // Elements used to test the functionality
    private Order order1;
    private Order order2;


    @BeforeEach
    public void init() {
        orderDao = new OrderDao(this.dslContext);

        order1 = orderDao.findById(1);
        order2 = orderDao.findById(2);
    }


    @Test
    public void getId_whenNullModelIsGiven_thenNullIdIsReturned() {
        // When
        Integer id = orderDao.getId(null);

        // Then
        assertNull(id);
    }


    @Test
    public void getId_whenNotNullModelIsGiven_thenItsIdIsReturned() {
        // When
        Integer id = orderDao.getId(order1);

        // Then
        assertNotNull(id);
        assertEquals(order1.getId(), id);
    }


    @Test
    public void findByIds_whenNullIdsAreGiven_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderDao.findByIds(null);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void findByIds_whenANonExistentIdsIsGiven_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderDao.findByIds(-2, -1);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void findByIds_whenAnExistentIdIsGiven_thenRelatedModelIsReturned() {
        // When
        List<Order> orders = orderDao.findByIds(order1.getId());

        // Then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertThat(orders.get(0), samePropertyValuesAs(order1));
    }


    @Test
    public void findByIds_whenExistentIdsAreGiven_thenRelatedModelsAreReturned() {
        // When
        List<Order> orders = orderDao.findByIds(order1.getId(), order2.getId());

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertThat(orders, contains(order1, order2));
    }


    @Test
    public void findOptionalById_whenNoIdIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findOptionalById(null);

        // Then
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void findOptionalById_whenANonExistentIdIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        Integer nonExistentId = -1;

        // When
        Optional<Order> optionalOrder = orderDao.findOptionalById(nonExistentId);

        // Then
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void findOptionalById_whenAnExistentIdIsGiven_thenOptionalWithRelatedModelIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findOptionalById(order1.getId());

        // Then
        assertTrue(optionalOrder.isPresent());
        assertThat(optionalOrder.get(), samePropertyValuesAs(order1));
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenNullIdIsGiven_thenEmptyOptionalIsReturned() {
        // When
        Optional<OrderDto> optionalOrderDto = orderDao.fetchToOrderDtoByIdWithOrderLineDto(null);

        // Then
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenANonExistentIdIsGiven_thenEmptyOptionalIsReturned() {
        // Given
        Integer nonExistentId = -1;

        // When
        Optional<OrderDto> optionalOrderDto = orderDao.fetchToOrderDtoByIdWithOrderLineDto(nonExistentId);

        // Then
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenAnExistentIdIsGiven_thenOptionalWithRelatedModelIsReturned() {
        // Given (information stored in test database)
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).orderId(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).orderId(1).pizza(hawaiian).cost(8D).amount((short)1).build();

        // When
        Optional<OrderDto> optionalOrderDto = orderDao.fetchToOrderDtoByIdWithOrderLineDto(order1.getId());

        // Then
        assertTrue(optionalOrderDto.isPresent());
        assertEquals(order1.getId(), optionalOrderDto.get().getId());
        assertEquals(order1.getCode(), optionalOrderDto.get().getCode());
        assertEquals(order1.getCreated(), optionalOrderDto.get().getCreated());
        assertThat(optionalOrderDto.get().getOrderLines(), containsInAnyOrder(orderLineDto1, orderLineDto2));
    }


    @Test
    public void findByCodes_whenNullCodesAreGiven_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderDao.findByCodes(null);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void findByCodes_whenANonExistentCodeIsGiven_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderDao.findByCodes(order1.getCode() + "V2", order2.getCode() + "V2");

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void findByCodes_whenAnExistentCodeIsGiven_thenRelatedModelIsReturned() {
        // When
        List<Order> orders = orderDao.findByCodes(order1.getCode());

        // Then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertThat(orders.get(0), samePropertyValuesAs(order1));
    }


    @Test
    public void findByCodes_whenExistentCodesAreGiven_thenRelatedModelsAreReturned() {
        // When
        List<Order> orders = orderDao.findByCodes(order1.getCode(), order2.getCode());

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertThat(orders, contains(order1, order2));
    }


    @Test
    public void findByCode_whenNoCodeIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findByCode(null);

        // Then
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void findByCode_whenANonExistentCodeIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String nonExistentCode = order1.getCode() + order2.getCode();

        // When
        Optional<Order> optionalOrder = orderDao.findByCode(nonExistentCode);

        // Then
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void findByCode_whenAnExistentCodeIsGiven_thenOptionalWithRelatedModelIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findByCode(order1.getCode());

        // Then
        assertTrue(optionalOrder.isPresent());
        assertThat(optionalOrder.get(), samePropertyValuesAs(order1));
    }


    @Test
    public void fetchPageToOrderDtoByIdWithOrderLineDto_whenNegativePageValueIsGiven_thenEmptySetIsReturned() {
        // When
        Set<OrderDto> orderds = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(-1, 2);

        // Then
        assertNotNull(orderds);
        assertTrue(orderds.isEmpty());
    }


    @Test
    public void fetchPageToOrderDtoByIdWithOrderLineDto_whenNegativeSizeValueIsGiven_thenEmptySetIsReturned() {
        // When
        Set<OrderDto> orderds = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(1, -2);

        // Then
        assertNotNull(orderds);
        assertTrue(orderds.isEmpty());
    }


    @Test
    public void fetchPageToOrderDtoByIdWithOrderLineDto_whenZeroSizeValueIsGiven_thenEmptySetIsReturned() {
        // When
        Set<OrderDto> orderds = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(1, 0);

        // Then
        assertNotNull(orderds);
        assertTrue(orderds.isEmpty());
    }


    @Test
    public void fetchPageToOrderDtoByIdWithOrderLineDto_whenDatabaseContainsMoreElementsThanGivenSizeAndPage_thenASubsetIsReturned() throws ParseException {
        // Given
        int page = 0;
        int size = 1;

        // Information stored in test database
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();
        PizzaDto margherita = PizzaDto.builder().id((short)3).name("Margherita").cost(7D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).orderId(1).pizza(carbonara).cost(7.5D).amount((short)1).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).orderId(1).pizza(hawaiian).cost(8D).amount((short)1).build();
        OrderLineDto orderLineDto3 = OrderLineDto.builder().id(3).orderId(2).pizza(carbonara).cost(7.5D).amount((short)1).build();
        OrderLineDto orderLineDto4 = OrderLineDto.builder().id(4).orderId(2).pizza(hawaiian).cost(16D).amount((short)2).build();
        OrderLineDto orderLineDto5 = OrderLineDto.builder().id(5).orderId(2).pizza(margherita).cost(21D).amount((short)3).build();

        OrderDto orderDto1 = OrderDto.builder().id(1).code("Order 1").created(new Timestamp(DATE_FORMAT.parse("2018-12-31 16:00:00.000000").getTime()))
                                                     .orderLines(Arrays.asList(orderLineDto1, orderLineDto2)).build();
        OrderDto orderDto2 = OrderDto.builder().id(2).code("Order 2").created(new Timestamp(DATE_FORMAT.parse("2019-01-02 18:00:00.000000").getTime()))
                                                     .orderLines(Arrays.asList(orderLineDto3, orderLineDto4, orderLineDto5)).build();
        // When
        Set<OrderDto> orderdsPage0 = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(page, size);
        Set<OrderDto> orderdsPage1 = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(page+1, size);

        // Then
        assertNotNull(orderdsPage0);
        assertEquals(size, orderdsPage0.size());
        for (OrderDto orderDto : orderdsPage0)
            assertThat(orderDto, samePropertyValuesAs(orderDto2));

        assertNotNull(orderdsPage1);
        assertEquals(size, orderdsPage1.size());
        for (OrderDto o : orderdsPage1)
            assertThat(o, samePropertyValuesAs(orderDto1));
    }


    @Test
    public void fetchPageToOrderDtoByIdWithOrderLineDto_whenAllDatabaseRowsAreRequired_thenAllDataIsReturned() throws ParseException {
        // Given
        int page = 0;
        int size = 2;

        // Information stored in test database
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();
        PizzaDto margherita = PizzaDto.builder().id((short)3).name("Margherita").cost(7D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).orderId(1).pizza(carbonara).cost(7.5D).amount((short)1).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).orderId(1).pizza(hawaiian).cost(8D).amount((short)1).build();
        OrderLineDto orderLineDto3 = OrderLineDto.builder().id(3).orderId(2).pizza(carbonara).cost(7.5D).amount((short)1).build();
        OrderLineDto orderLineDto4 = OrderLineDto.builder().id(4).orderId(2).pizza(hawaiian).cost(16D).amount((short)2).build();
        OrderLineDto orderLineDto5 = OrderLineDto.builder().id(5).orderId(2).pizza(margherita).cost(21D).amount((short)3).build();

        OrderDto orderDto1 = OrderDto.builder().id(1).code("Order 1").created(new Timestamp(DATE_FORMAT.parse("2018-12-31 16:00:00.000000").getTime()))
                                                     .orderLines(Arrays.asList(orderLineDto1, orderLineDto2)).build();
        OrderDto orderDto2 = OrderDto.builder().id(2).code("Order 2").created(new Timestamp(DATE_FORMAT.parse("2019-01-02 18:00:00.000000").getTime()))
                                                     .orderLines(Arrays.asList(orderLineDto3, orderLineDto4, orderLineDto5)).build();
        // When
        Set<OrderDto> orderdsPage = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(page, size);

        // Then
        assertNotNull(orderdsPage);
        assertEquals(size, orderdsPage.size());
        assertEquals(orderDao.count(), orderdsPage.size());

        Set<Integer> ordersFound = new HashSet<>();
        for (OrderDto o : orderdsPage) {
            if (o.getId().equals(orderDto1.getId())) {
                ordersFound.add(o.getId());
                assertThat(o, samePropertyValuesAs(orderDto1));
            }
            if (o.getId().equals(orderDto2.getId())) {
                ordersFound.add(o.getId());
                assertThat(o, samePropertyValuesAs(orderDto2));
            }
        }
        assertEquals(orderdsPage.size(), ordersFound.size());
    }

}
