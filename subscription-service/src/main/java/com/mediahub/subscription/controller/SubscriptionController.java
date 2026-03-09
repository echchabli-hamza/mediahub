package com.mediahub.subscription.controller;

import com.mediahub.subscription.dto.SubscriptionRequest;
import com.mediahub.subscription.dto.SubscriptionResponse;
import com.mediahub.subscription.dto.SubscriptionStatusResponse;
import com.mediahub.subscription.dto.UserInfoResponse;
import com.mediahub.subscription.service.SubscriptionService;
import com.mediahub.subscription.service.WebClientUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final WebClientUserService webClientUserService;

    public SubscriptionController(SubscriptionService subscriptionService,
                                   WebClientUserService webClientUserService) {
        this.subscriptionService = subscriptionService;
        this.webClientUserService = webClientUserService;
    }

    /**
     * Create a new subscription
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(subscriptionService.create(request));
    }

    /**
     * Get subscription by ID
     */
    @GetMapping("/{id}")
    public SubscriptionResponse getById(@PathVariable Long id) {
        return subscriptionService.getById(id);
    }

    /**
     * Get all subscriptions
     */
    @GetMapping
    public List<SubscriptionResponse> getAll() {
        return subscriptionService.getAll();
    }

    /**
     * Get all subscriptions for a user
     */
    @GetMapping("/user/{userId}")
    public List<SubscriptionResponse> getByUserId(@PathVariable Long userId) {
        return subscriptionService.getByUserId(userId);
    }

    /**
     * Check subscription status for a user
     */
    @GetMapping("/user/{userId}/status")
    public SubscriptionStatusResponse checkStatus(@PathVariable Long userId) {
        return subscriptionService.checkStatus(userId);
    }

    /**
     * Cancel a subscription
     */
    @PutMapping("/{id}/cancel")
    public SubscriptionResponse cancel(@PathVariable Long id) {
        return subscriptionService.cancel(id);
    }

    /**
     * Renew a subscription
     */
    @PutMapping("/{id}/renew")
    public SubscriptionResponse renew(@PathVariable Long id) {
        return subscriptionService.renew(id);
    }

    /**
     * Delete a subscription
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Inter-service endpoints ----

    /**
     * Check if user has active subscription (for other services)
     */
    @GetMapping("/user/{userId}/active")
    public Map<String, Boolean> hasActiveSubscription(@PathVariable Long userId) {
        return Map.of("active", subscriptionService.hasActiveSubscription(userId));
    }

    // ---- WebClient endpoints (reactive) ----

    /**
     * Get user info using WebClient (reactive call to user-service)
     */
    @GetMapping("/user/{userId}/info-reactive")
    public Mono<UserInfoResponse> getUserInfoReactive(@PathVariable Long userId) {
        return webClientUserService.getUserInfoReactive(userId);
    }

    /**
     * Check user existence using WebClient (reactive)
     */
    @GetMapping("/user/{userId}/exists-reactive")
    public Mono<Map<String, Boolean>> userExistsReactive(@PathVariable Long userId) {
        return webClientUserService.userExistsReactive(userId)
            .map(exists -> Map.of("exists", exists));
    }
}
