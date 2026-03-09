package com.mediahub.subscription.client;

import com.mediahub.subscription.dto.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign Client for communicating with media-service
 */
@FeignClient(name = "MEDIA-SERVICE")
public interface MediaServiceClient {

    @GetMapping("/media/{id}/exists")
    Map<String, Boolean> mediaExists(@PathVariable("id") Long mediaId);
}
