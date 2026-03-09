package com.mediahub.subscription.service;

import com.mediahub.subscription.dto.UserInfoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Service using WebClient for reactive inter-service communication
 */
@Service
public class WebClientUserService {

    private final WebClient.Builder webClientBuilder;

    public WebClientUserService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Check if user exists using WebClient (reactive)
     */
    public Mono<Boolean> userExistsReactive(Long userId) {
        return webClientBuilder.build()
            .get()
            .uri("http://USER-SERVICE/users/{id}/exists", userId)
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> Boolean.TRUE.equals(response.get("exists")))
            .onErrorReturn(false);
    }

    /**
     * Get user info using WebClient (reactive)
     */
    public Mono<UserInfoResponse> getUserInfoReactive(Long userId) {
        return webClientBuilder.build()
            .get()
            .uri("http://USER-SERVICE/users/{id}/info", userId)
            .retrieve()
            .bodyToMono(UserInfoResponse.class)
            .onErrorReturn(new UserInfoResponse());
    }
}
