package com.example.demo.Services;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.Models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<User> fetchUser(User user) {
        User _user = userRepository.findByLogin(user.getLogin());

        if (_user != null && _user.getPassword().equals(user.getPassword())) {
            return new ResponseEntity<>(_user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<User> addUser(User user) {
        try {
            User _user = userRepository.save(new User(user.getLogin(), user.getPassword(), user.getEmail()));
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> editPassword(String login, String newPassword, String currentPassword) {
        try {
            User user = userRepository.findByLogin(login);
            if (user != null) {
                boolean correctPassword = currentPassword.equals(user.getPassword());
                if (correctPassword) {
                    user.setPassword(newPassword);
                    userRepository.save(user);

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "User password changed successfully");

                    return ResponseEntity.ok(response);
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Wrong password");

                    return ResponseEntity.ok(response);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("Error while changing User password: " + e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
