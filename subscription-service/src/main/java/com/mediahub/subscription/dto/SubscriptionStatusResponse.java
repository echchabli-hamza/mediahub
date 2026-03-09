package com.mediahub.subscription.dto;

import com.mediahub.subscription.model.SubscriptionStatus;

public class SubscriptionStatusResponse {

    private Long userId;
    private boolean hasActiveSubscription;
    private SubscriptionStatus status;
    private String message;

    public SubscriptionStatusResponse() {}

    public SubscriptionStatusResponse(Long userId, boolean hasActiveSubscription, 
                                       SubscriptionStatus status, String message) {
        this.userId = userId;
        this.hasActiveSubscription = hasActiveSubscription;
        this.status = status;
        this.message = message;
    }

    // ---- Getters & Setters ----

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isHasActiveSubscription() { return hasActiveSubscription; }
    public void setHasActiveSubscription(boolean hasActiveSubscription) { 
        this.hasActiveSubscription = hasActiveSubscription; 
    }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
