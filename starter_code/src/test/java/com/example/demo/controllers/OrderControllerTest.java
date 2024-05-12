package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.example.demo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    // TODO: order requests failures

    private OrderController orderController;
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    void submitTest() {
        User user = createUser(USERNAME, PASSWORD);
        createCart(user);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder retrievedUserOrder = response.getBody();
        assertNotNull(retrievedUserOrder);
        assertNotNull(retrievedUserOrder.getItems());
        assertNotNull(retrievedUserOrder.getTotal());
        assertNotNull(retrievedUserOrder.getUser());
    }

    @Test
    void getOrdersForUserTest() {
        User user = createUser(USERNAME, PASSWORD);
        List<UserOrder> orders = createOrders(user.getUsername(), PASSWORD);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<UserOrder> retrievedOrders = responseEntity.getBody();
        assertNotNull(retrievedOrders);
        assertEquals(orders.size(), retrievedOrders.size());

    }
}
