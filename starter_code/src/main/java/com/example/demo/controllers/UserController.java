package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/user")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        logger.info("Request received to create user with username: {}", createUserRequest.getUsername());
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.warn("Invalid password or password mismatch for user: {}", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        logger.info("User created successfully with username: {}", createUserRequest.getUsername());
        return ResponseEntity.ok(user);
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        logger.info("Request received to retrieve user by ID: {}", id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        logger.info("Request received to retrieve user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        } else {
            logger.info("User found with username: {}", username);
            return ResponseEntity.ok(user);
        }
        // return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

}
