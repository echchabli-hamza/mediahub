package com.mediahub.mediaService.dto;

import com.mediahub.mediaService.model.Category;
import java.time.LocalDateTime;

public record MediaResponse(
        Long id,
        String title,
        String description,
        String genre,
        Category category,
        Integer releaseYear,
        Integer duration,
        Double rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
