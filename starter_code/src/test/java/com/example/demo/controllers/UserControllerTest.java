package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.demo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    // TODO: CreateUser request failures

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccessfully() {
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(PASSWORD_HASHED);
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(USERNAME);
        userRequest.setPassword(PASSWORD);
        userRequest.setConfirmPassword(PASSWORD);
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(USERNAME, u.getUsername());
        assertEquals(PASSWORD_HASHED, u.getPassword());
    }

    @Test
    public void findById() {
        Long id = 1L;
        User user = TestUtils.createUser(USERNAME, PASSWORD);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> responseEntity = userController.findById(id);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User userBody = responseEntity.getBody();
        assertNotNull(userBody);
        assertEquals(id, Long.valueOf(userBody.getId()));
        assertEquals(USERNAME, userBody.getUsername());
        assertEquals(PASSWORD, userBody.getPassword());
    }

    @Test
    public void findByUsername() {
        Long id = 1L;
        User user = TestUtils.createUser(USERNAME, PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.findByUserName(USERNAME);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User userBody = responseEntity.getBody();
        assertNotNull(userBody);
        assertEquals(id, Long.valueOf(userBody.getId()));
        assertEquals(USERNAME, userBody.getUsername());
        assertEquals(PASSWORD, userBody.getPassword());
    }
}
