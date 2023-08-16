package com.example.demo.Controllers;

import com.example.demo.Models.User;
import com.example.demo.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> fetchAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<User> fetchUser(@RequestBody User user) {
        return userService.fetchUser(user);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/editPassword/{login}/{currentPassword}/{newPassword}")
    public ResponseEntity<Map<String, String>> editPassword(@PathVariable("login") String login, @PathVariable("newPassword") String newPassword,
                                                            @PathVariable("currentPassword") String currentPassword) {
        return userService.editPassword(login, newPassword, currentPassword);
    }
}
