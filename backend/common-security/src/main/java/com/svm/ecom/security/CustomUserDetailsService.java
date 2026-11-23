
package com.svm.ecom.security;

import com.svm.ecom.domain.user.AppUser;
import com.svm.ecom.infrastructure.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Custom UserDetailsService that loads user information from the database.
 * This service is used by Spring Security for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user details from the database by username.
     * This method is called by Spring Security during authentication.
     *
     * @param username the username to authenticate
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user is not found in database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        AppUser appUser = userOpt.get();
        
        // Build UserDetails object for Spring Security
        // Password should already be BCrypt encoded in the database
        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword()) // Already encoded password from database
                .roles(appUser.getRole())
                .build();
    }
}
