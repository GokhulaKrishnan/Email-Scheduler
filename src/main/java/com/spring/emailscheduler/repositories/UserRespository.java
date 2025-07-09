package com.spring.emailscheduler.repositories;

import com.spring.emailscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
