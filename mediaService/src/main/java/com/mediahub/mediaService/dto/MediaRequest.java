package com.mediahub.mediaService.dto;

import com.mediahub.mediaService.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaRequest(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        String genre,

        @NotNull(message = "Category is required")
        Category category,

        Integer releaseYear,

        Integer duration
) {}
