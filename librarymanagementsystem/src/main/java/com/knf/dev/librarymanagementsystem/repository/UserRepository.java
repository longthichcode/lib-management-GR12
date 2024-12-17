package com.knf.dev.librarymanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.knf.dev.librarymanagementsystem.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email); 

    List<User> findByFirstName(String firstName); 

    List<User> findByLastName(String lastName); 
}