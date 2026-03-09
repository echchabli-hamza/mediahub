package com.mediahub.mediaService.dto;

import com.mediahub.mediaService.model.Category;

public record MediaInfoResponse(
        Long id,
        String title,
        Category category,
        String genre
) {}
