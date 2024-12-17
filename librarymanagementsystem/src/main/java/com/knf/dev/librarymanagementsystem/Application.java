package com.knf.dev.librarymanagementsystem;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;

@SpringBootApplication
public class Application {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner initialCreate() {
	    return (args) -> {
	        if (userRepository.findByEmail("btln12") == null) {
	            var user = new User("admin", "admin", "btln12", passwordEncoder.encode("abcd@1234"),
	                    Arrays.asList(new Role("ROLE_ADMIN")));
	            userRepository.save(user);
	            System.out.println("Admin user created successfully!");
	        } else {
	            System.out.println("User with email 'btln12' already exists!");
	        }
	    };
	}

}
