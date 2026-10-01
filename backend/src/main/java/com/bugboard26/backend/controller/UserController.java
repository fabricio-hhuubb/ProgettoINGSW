package com.bugboard26.backend.controller;

import com.bugboard26.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    //Record Useroption
    public record UserOption(Long id, String email) {}
}