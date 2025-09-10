package org.learnspring.expensetracker.Service;

import org.learnspring.expensetracker.Model.MyUserPrincipal;
import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        Users users = repo.findByUsername(username);
        if(users == null){
            System.out.println("No user found with username: " + username);
            throw new UsernameNotFoundException("no user found");
        }
        System.out.println("User found: " + users.getUsername() + " with encoded password");
        return new MyUserPrincipal(users);
    }
}
