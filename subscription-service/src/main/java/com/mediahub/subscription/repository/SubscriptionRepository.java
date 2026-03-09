package com.mediahub.subscription.repository;

import com.mediahub.subscription.model.Subscription;
import com.mediahub.subscription.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    List<Subscription> findByStatus(SubscriptionStatus status);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
