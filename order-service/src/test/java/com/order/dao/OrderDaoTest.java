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

}
