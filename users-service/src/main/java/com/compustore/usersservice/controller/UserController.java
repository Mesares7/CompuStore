package com.compustore.usersservice.controller;

import com.compustore.usersservice.dto.UserProfileResponse;
import com.compustore.usersservice.model.User;
import com.compustore.usersservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing user profile information.  The endpoint requires a
 * valid JWT.  The authenticated username is extracted from the
 * Authentication object and used to look up the user in the repository.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponse> profile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        UserProfileResponse response = new UserProfileResponse(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }
}