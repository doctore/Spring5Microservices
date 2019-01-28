package com.order.dao;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@JooqTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class OrderDaoTest {

    @Autowired
    private DSLContext dslContext;

    private OrderDao orderDao;

    // Elements used to test the functionality
    private Order order1;
    private Order order2;


    @Before
    public void init() {
        orderDao = new OrderDao(this.dslContext);

        order1 = orderDao.findById(1);
        order2 = orderDao.findById(2);
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
    public void findByIds_whenAnExistentIdIsGiven_thenRelatedEntityIsReturned() {
        // When
        List<Order> orders = orderDao.findByIds(order1.getId());

        // Then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertThat(order1, samePropertyValuesAs(orders.get(0)));
    }


    @Test
    public void findByIds_whenExistentIdsAreGiven_thenRelatedEntitiesAreReturned() {
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
        Integer nonExistentId = order1.getId() + order2.getId();

        // When
        Optional<Order> optionalOrder = orderDao.findOptionalById(nonExistentId);

        // Then
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void findOptionalById_whenAnExistentIdIsGiven_thenNonEmptyOptionalIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findOptionalById(order1.getId());

        // Then
        assertTrue(optionalOrder.isPresent());
        assertThat(order1, samePropertyValuesAs(optionalOrder.get()));
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenNullIdIsGiven_thenEmptyOptionalIsReturned() throws SQLException {
        // When
        Optional<OrderDto> optionalOrderDto = orderDao.fetchToOrderDtoByIdWithOrderLineDto(null);

        // Then
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenANonExistentIdIsGiven_thenOptionalEmptyIsReturned() throws SQLException {
        // Given
        Integer nonExistentId = order1.getId() + order2.getId();

        // When
        Optional<OrderDto> optionalOrderDto = orderDao.fetchToOrderDtoByIdWithOrderLineDto(null);

        // Then
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void fetchToOrderDtoByIdWithOrderLineDto_whenAnExistentIdIsGiven_thenNonEmptyOptionalIsReturned() throws SQLException {
        // Given (information stored in test database)
        PizzaDto carbonara = PizzaDto.builder().id(1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id(1).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).pizza(hawaiian).cost(8D).amount((short)1).build();

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
    public void findByCodes_whenAnExistentCodeIsGiven_thenRelatedEntityIsReturned() {
        // When
        List<Order> orders = orderDao.findByCodes(order1.getCode());

        // Then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertThat(order1, samePropertyValuesAs(orders.get(0)));
    }


    @Test
    public void findByCodes_whenExistentCodesAreGiven_thenRelatedEntitiesAreReturned() {
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
    public void findByCode_whenAnExistentCodeIsGiven_thenNonEmptyOptionalIsReturned() {
        // When
        Optional<Order> optionalOrder = orderDao.findByCode(order1.getCode());

        // Then
        assertTrue(optionalOrder.isPresent());
        assertThat(order1, samePropertyValuesAs(optionalOrder.get()));
    }

}
