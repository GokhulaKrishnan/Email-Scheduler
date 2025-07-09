package com.spring.emailscheduler.controller;

// This class contains the endpoint for the users

import com.spring.emailscheduler.model.User;
import com.spring.emailscheduler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to get All the users in the application
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint to get Users by Id
    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Get users by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email){
        User user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Creating user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User userData){

        User user = userService.createUser(userData);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Updating user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userData){
        User user = userService.updateUser(id, userData);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Deleting user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        String message = userService.deleteUserById(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
