package com.mediahub.subscription.service;

import com.mediahub.subscription.client.UserServiceClient;
import com.mediahub.subscription.dto.SubscriptionRequest;
import com.mediahub.subscription.dto.SubscriptionResponse;
import com.mediahub.subscription.dto.SubscriptionStatusResponse;
import com.mediahub.subscription.model.Subscription;
import com.mediahub.subscription.model.SubscriptionStatus;
import com.mediahub.subscription.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserServiceClient userServiceClient;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                                UserServiceClient userServiceClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Create a new subscription for a user
     */
    public SubscriptionResponse create(SubscriptionRequest request) {
        // Verify user exists via Feign
        try {
            Map<String, Boolean> response = userServiceClient.userExists(request.getUserId());
            if (response == null || !Boolean.TRUE.equals(response.get("exists"))) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "User not found with id: " + request.getUserId());
            }
        } catch (Exception e) {
            if (e instanceof ResponseStatusException) throw e;
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "Unable to verify user. User service unavailable.");
        }

        // Check if user already has an active subscription
        if (subscriptionRepository.existsByUserIdAndStatus(request.getUserId(), SubscriptionStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "User already has an active subscription");
        }

        Subscription subscription = new Subscription(request.getUserId(), request.getPlan());
        subscription = subscriptionRepository.save(subscription);
        
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Get subscription by ID
     */
    public SubscriptionResponse getById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Subscription not found with id: " + id));
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Get all subscriptions for a user
     */
    public List<SubscriptionResponse> getByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
            .map(SubscriptionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Check subscription status for a user
     */
    public SubscriptionStatusResponse checkStatus(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
            .map(sub -> {
                // Check if subscription has expired
                if (sub.getEndDate().isBefore(LocalDateTime.now())) {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(sub);
                    return new SubscriptionStatusResponse(userId, false, 
                        SubscriptionStatus.EXPIRED, "Subscription has expired");
                }
                return new SubscriptionStatusResponse(userId, true, 
                    SubscriptionStatus.ACTIVE, "Active subscription found");
            })
            .orElse(new SubscriptionStatusResponse(userId, false, 
                null, "No active subscription found"));
    }

    /**
     * Cancel a subscription
     */
    public SubscriptionResponse cancel(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription = subscriptionRepository.save(subscription);
        
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Renew a subscription
     */
    public SubscriptionResponse renew(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Subscription not found with id: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription = subscriptionRepository.save(subscription);
        
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Get all subscriptions
     */
    public List<SubscriptionResponse> getAll() {
        return subscriptionRepository.findAll().stream()
            .map(SubscriptionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Delete a subscription
     */
    public void delete(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    /**
     * Check if user has active subscription (for inter-service calls)
     */
    public boolean hasActiveSubscription(Long userId) {
        return subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }
}
