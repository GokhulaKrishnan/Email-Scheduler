package com.spring.emailscheduler.service;

import com.spring.emailscheduler.exceptions.APIExceptionHandler;
import com.spring.emailscheduler.exceptions.ResourceNotFoundException;
import com.spring.emailscheduler.model.User;
import com.spring.emailscheduler.repositories.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRespository userRespository;

    // To get a particular user by id
    @Override
    public User getUserById(Long id) {

        User user = userRespository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return user;
    }

    // To get all the users
    @Override
    public List<User> getAllUsers() {

        List<User> users = userRespository.findAll();

        if (users.isEmpty()) {
            throw new APIExceptionHandler("No Users Found");
        }
        return users;
    }

    // Getting a user by email
    @Override
    public User getUserByEmail(String email) {

        User user = userRespository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        return user;
    }

    // Creating a new user
    @Override
    public User createUser(User userData) {

        // Checking if there exists a user
        User userDb = userRespository.findByEmail(userData.getEmail());

        // If there exists a user in the db throwing error
        if(userDb != null) {
            throw new APIExceptionHandler("User already exists");
        }

        // Creating a new user
        User newUser = userRespository.save(userData);
        return newUser;
    }

    // Updating the User
    @Override
    public User updateUser(Long id, User userData) {

        // Getting the user by id
        User user = userRespository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Updating the fields
        user.setEmail(userData.getEmail());
        user.setName(userData.getName());
        user.setTimeZone(userData.getTimeZone());

        User updatedUser = userRespository.save(user);
        return updatedUser;
    }

    // Deleting the user
    @Override
    public String deleteUserById(Long id) {

        // Checking if the user exists
        User user = userRespository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRespository.deleteById(id);
        return "User deleted successfully";
    }


}
