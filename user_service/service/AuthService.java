package com.mediahub.user_service.service;

import com.mediahub.user_service.dto.AuthRequest;
import com.mediahub.user_service.dto.AuthResponse;
import com.mediahub.user_service.dto.RegisterRequest;
import com.mediahub.user_service.dto.UserDTO;
import com.mediahub.user_service.model.Role;
import com.mediahub.user_service.model.User;
import com.mediahub.user_service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            PasswordService passwordService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user. Role always defaults to USER.
     */
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username already taken: " + request.getUsername());
        }
        String hashed = passwordService.encode(request.getPassword());
        User user = new User(request.getUsername(), hashed, Role.USER);
        userRepository.save(user);
    }

    /**
     * Login: verify credentials and return a signed JWT.
     */
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Fetch all users. Should be called only by ADMIN.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * Check if user exists by ID
     */
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Get user info by ID (for inter-service communication)
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return new UserDTO(user.getId(), user.getUsername(), user.getRole());
    }
}
