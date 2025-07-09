package com.spring.emailscheduler.service;

import com.spring.emailscheduler.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    User createUser(User user);

    User updateUser(Long id, User userData);

    String deleteUserById(Long id);
}
