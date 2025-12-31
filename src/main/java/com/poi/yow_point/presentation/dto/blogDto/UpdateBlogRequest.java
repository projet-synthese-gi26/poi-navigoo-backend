package com.poi.yow_point.presentation.dto.blogDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateBlogRequest {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("content")
    private String content;

    @JsonProperty("is_active")
    private Boolean isActive;
}
