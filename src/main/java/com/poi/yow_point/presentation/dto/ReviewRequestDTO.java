package com.poi.yow_point.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    private UUID userId;
    private String platformType;
    private Integer rating;
    private String reviewText;
    @Builder.Default
    private Integer likes = 0;
    @Builder.Default
    private Integer dislikes = 0;
}
