package com.example.regapp.security;

import com.example.regapp.entity.User;
import com.example.regapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findUserByIdentifier(identifier);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + identifier + " is not found");
        }

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getIdentifier())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
