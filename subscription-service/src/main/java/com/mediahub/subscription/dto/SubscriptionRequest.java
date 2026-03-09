package com.mediahub.subscription.dto;

import com.mediahub.subscription.model.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;

    public SubscriptionRequest() {}

    public SubscriptionRequest(Long userId, SubscriptionPlan plan) {
        this.userId = userId;
        this.plan = plan;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
}
