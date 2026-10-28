package com.bugboard26.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugboard26.backend.dto.UserOption;
import com.bugboard26.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserOption> getUsers() {
        List<UserOption> users = new ArrayList<>();

        for (var user : userRepository.findAll()) {
            users.add(new UserOption(user.getId(), user.getEmail()));
        }

        return users;
    }

}