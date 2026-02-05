package com.moneymanager.backend.controller;

import com.moneymanager.backend.model.User;
import com.moneymanager.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/dummy")
    public User getOrCreateDummyUser() {
        return userRepository.findByEmail("guest@example.com")
                .orElseGet(() -> {
                    User dummy = new User();
                    dummy.setName("Guest User");
                    dummy.setEmail("guest@example.com");
                    return userRepository.save(dummy);
                });
    }
}
