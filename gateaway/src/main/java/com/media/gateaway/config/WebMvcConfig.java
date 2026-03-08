package com.media.gateaway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // JwtAuthFilter is now a standard Servlet Filter and auto-registered by Spring
    // Boot.
    // No need to register it as an interceptor here.
}
