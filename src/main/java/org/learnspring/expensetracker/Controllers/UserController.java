package org.learnspring.expensetracker.Controllers;

import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.Service.UserService;
import org.learnspring.expensetracker.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService service;
    
    @Autowired
    private UserRepo userRepo;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Users user){
        try {
            logger.info("Registration attempt for username: {}", user.getUsername());
            
            // Check if username already exists
            if (userRepo.findByUsername(user.getUsername()) != null) {
                logger.warn("Registration failed - username already exists: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"error\":\"Username already exists\"}");
            }
            
            Users registeredUser = service.register(user);
            logger.info("User registered successfully: {}", registeredUser.getUsername());
            
            // Don't return password in response
            registeredUser.setPassword(null);
            return ResponseEntity.ok(registeredUser);
            
        } catch (Exception e) {
            logger.error("Registration failed for username: {}, error: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Registration failed\"}");
        }
    }
}
