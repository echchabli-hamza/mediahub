package com.mediahub.subscription.client;

import com.mediahub.subscription.dto.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign Client for communicating with user-service
 */
@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/users/{id}/exists")
    Map<String, Boolean> userExists(@PathVariable("id") Long userId);

    @GetMapping("/users/{id}/info")
    UserInfoResponse getUserInfo(@PathVariable("id") Long userId);
}
