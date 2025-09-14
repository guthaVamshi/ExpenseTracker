package org.learnspring.expensetracker.Service;

import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;
    
    @Autowired
    private BCryptPasswordEncoder encoder;
    public Users register(Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        // Set default role as USER for new registrations
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        return repo.save(user);
    }
}
