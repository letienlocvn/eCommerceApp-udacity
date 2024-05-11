package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.demo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CartControllerTest {
    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }


    @Test
    void addToCart() {
        User user = createUser(USERNAME, PASSWORD);
        Item item = createItem(1L);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(10);

        ResponseEntity<Cart> responseEntity = cartController.addToCart(cartRequest);
        assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

        Cart cartRetrieved = responseEntity.getBody();
        assertNotNull(cartRetrieved);
        Assertions.assertEquals(Optional.of(1L).get(), cartRetrieved.getId());

        Assertions.assertEquals(createItem(1L), cartRetrieved.getItems().get(0));

        Cart newCart = createCart(user);
        Assertions.assertEquals(newCart.getItems().size() + cartRequest.getQuantity(),
                cartRetrieved.getItems().size());


        Item itemCart = createItem(cartRequest.getItemId());
        Assertions.assertEquals(
                itemCart.getPrice()
                        .multiply(BigDecimal.valueOf(cartRequest.getQuantity()))
                        .add(newCart.getTotal()),
                cartRetrieved.getTotal());

    }

    @Test
    void removeFromCart() {
        User user = createUser(USERNAME, PASSWORD);
        Item item = createItem(1L);
        Cart cart = createCart(user);
        cart.addItem(item);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> responseEntity = cartController.removeFromCart(cartRequest);
        assertNotNull(responseEntity);

        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart cartRetrieved = responseEntity.getBody();
        Cart compareCart = createCart(user);
        assertNotNull(cartRetrieved);

        Item itemRetrieved = createItem(cartRetrieved.getId());
        BigDecimal itemPrice = itemRetrieved.getPrice();
        BigDecimal expectTotal = compareCart.getTotal()
                .subtract(itemPrice
                        .multiply(BigDecimal.valueOf(
                                cartRequest.getQuantity()))
                );

        Assertions.assertEquals(USERNAME, cartRetrieved.getUser().getUsername());
        Assertions.assertEquals(compareCart.getItems().size() - cartRequest.getQuantity(),
                cartRetrieved.getItems().size());
        Assertions.assertEquals(createItem(2), cartRetrieved.getItems().get(0));
        Assertions.assertEquals(expectTotal, cartRetrieved.getTotal());

        verify(cartRepository, times(1)).save(cartRetrieved);

    }
}