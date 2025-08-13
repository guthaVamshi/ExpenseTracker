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
        Users users = repo.findByusername(username);
        if(users == null){
            System.out.println("No user found");
            throw new UsernameNotFoundException("no user found");
        }
        return new MyUserPrincipal(users);

    }
}
