package com.compustore.usersservice.service;

import com.compustore.usersservice.model.Role;
import com.compustore.usersservice.model.User;
import com.compustore.usersservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service providing user registration and lookup functionality.  Business
 * operations are separated from the controllers so they can be reused and
 * tested independently.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the given username, raw password and role.
     * The password is encoded before persisting.  If a user with the same
     * username exists an IllegalArgumentException is thrown.
     *
     * @param username the username
     * @param password the plain text password
     * @param role     the role for the user
     * @return the persisted User
     */
    @Transactional
    public User register(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        String hashed = passwordEncoder.encode(password);
        User user = new User(username, hashed, role);
        return userRepository.save(user);
    }
}