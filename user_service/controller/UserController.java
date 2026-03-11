package com.mediahub.user_service.controller;

import com.mediahub.user_service.dto.AuthRequest;
import com.mediahub.user_service.dto.AuthResponse;
import com.mediahub.user_service.dto.RegisterRequest;
import com.mediahub.user_service.dto.UserDTO;
import com.mediahub.user_service.model.Role;
import com.mediahub.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    // ── Auth endpoints (public — gateway lets /auth/** through) ──────────────

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── Protected endpoints (gateway validates JWT before forwarding) ─────────

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable Long id) {
        return "from user-service: user id = " + id;
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers(@RequestHeader(value = "X-Role", required = false) String role) {
        if (role == null || !role.equals(Role.ADMIN.name())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can access this resource");
        }
        return authService.getAllUsers();
    }

    // ── Inter-service endpoints (for Feign clients) ─────────────────────────

    @GetMapping("/users/{id}/exists")
    public Map<String, Boolean> userExists(@PathVariable Long id) {
        return Map.of("exists", authService.userExists(id));
    }

    @GetMapping("/users/{id}/info")
    public UserDTO getUserInfo(@PathVariable Long id) {
        return authService.getUserById(id);
    }
}