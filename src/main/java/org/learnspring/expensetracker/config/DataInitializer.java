package org.learnspring.expensetracker.config;

import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (userRepo.findByUsername("admin") == null) {
            Users admin = new Users();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin"));
            admin.setRole("ADMIN");
            userRepo.save(admin);
            System.out.println("Created admin user: admin/admin");
        }

        // Create test user if it doesn't exist
        if (userRepo.findByUsername("test") == null) {
            Users test = new Users();
            test.setUsername("test");
            test.setPassword(encoder.encode("test"));
            test.setRole("USER");
            userRepo.save(test);
            System.out.println("Created test user: test/test");
        }
    }
}
