package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.poiReview.PoiReviewService;
import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api-reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "POI Reviews", description = "API for managing reviews and comments on Points of Interest")
public class PoiReviewController {

        private final PoiReviewService poiReviewService;

        @PostMapping
        @Operation(summary = "Create a new review", description = "Creates a new review for a Point of Interest (POI)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Review created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
                        @ApiResponse(responseCode = "422", description = "Validation error", content = @Content)
        })
        public Mono<ResponseEntity<PoiReviewDTO>> createReview(
                        @RequestBody(description = "Data for the review to create", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))) @org.springframework.web.bind.annotation.RequestBody PoiReviewDTO reviewDTO) {
                log.info("POST /api-reviews - Creating review for POI: {}", reviewDTO.getPoiId());

                return poiReviewService.createReview(reviewDTO)
                                .map(createdReview -> ResponseEntity.status(HttpStatus.CREATED).body(createdReview))
                                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }

        @GetMapping("/{review_id}")
        @Operation(summary = "Get a review by ID", description = "Retrieves a specific review by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Review found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        public Mono<ResponseEntity<PoiReviewDTO>> getReviewById(
                        @Parameter(description = "Unique ID of the review", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("review_id") UUID reviewId) {
                log.info("GET /api-reviews/{} - Fetching review", reviewId);

                return poiReviewService.getReviewById(reviewId)
                                .map(review -> ResponseEntity.ok(review))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @GetMapping
        @Operation(summary = "Get all reviews", description = "Retrieves a complete list of all reviews")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of reviews retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class)))
        })
        public Flux<PoiReviewDTO> getAllReviews() {
                log.info("GET /api-reviews - Fetching all reviews");

                return poiReviewService.getAllReviews();
        }

        @GetMapping("/poi/{poi_id}/reviews")
        @Operation(summary = "Get reviews for a POI", description = "Retrieves all reviews associated with a specific Point of Interest")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of POI reviews retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class)))
        })
        public Flux<PoiReviewDTO> getReviewsByPoiId(
                        @Parameter(description = "Unique ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.info("GET /api-reviews/poi/{}/reviews - Fetching reviews for POI", poiId);

                return poiReviewService.getReviewsByPoiId(poiId);
        }

        @GetMapping("/user/{user_id}/reviews")
        @Operation(summary = "Get reviews by user", description = "Retrieves all reviews created by a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of user reviews retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class)))
        })
        public Flux<PoiReviewDTO> getReviewsByUserId(
                        @Parameter(description = "Unique ID of the user", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("user_id") UUID userId) {
                log.info("GET /api-reviews/user/{}/reviews - Fetching reviews for user", userId);

                return poiReviewService.getReviewsByUserId(userId);
        }

        @GetMapping("/organization/{organization_id}/reviews")
        @Operation(summary = "Get reviews by organization", description = "Retrieves all reviews associated with POIs of a specific organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of organization reviews retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class)))
        })
        public Flux<PoiReviewDTO> getReviewsByOrganizationId(
                        @Parameter(description = "Unique ID of the organization", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organization_id") UUID organizationId) {
                log.info("GET /api-reviews/organization/{}/reviews - Fetching reviews for organization",
                                organizationId);

                return poiReviewService.getReviewsByOrganizationId(organizationId);
        }

        @PutMapping("/{review_id}")
        @Operation(summary = "Update a review", description = "Updates an existing review completely")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Review updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
        })
        public Mono<ResponseEntity<PoiReviewDTO>> updateReview(
                        @Parameter(description = "Unique ID of the review to update", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("review_id") UUID reviewId,
                        @RequestBody(description = "New data for the review", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))) @org.springframework.web.bind.annotation.RequestBody PoiReviewDTO reviewDTO) {
                log.info("PUT /api-reviews/{} - Updating review", reviewId);

                return poiReviewService.updateReview(reviewId, reviewDTO)
                                .map(updatedReview -> ResponseEntity.ok(updatedReview))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{review_id}")
        @Operation(summary = "Delete a review", description = "Permanently deletes a review")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Review deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        public Mono<ResponseEntity<Void>> deleteReview(
                        @Parameter(description = "Unique ID of the review to delete", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("review_id") UUID reviewId) {
                log.info("DELETE /api-reviews/{} - Deleting review", reviewId);

                return poiReviewService.deleteReview(reviewId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @GetMapping("/poi/{poi_id}/average-rating")
        @Operation(summary = "Calculate the average rating for a POI", description = "Calculates and returns the average rating from all reviews for a POI")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Average rating calculated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Double.class, example = "4.2")))
        })
        public Mono<ResponseEntity<Double>> getAverageRatingByPoiId(
                        @Parameter(description = "Unique ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.info("GET /api-reviews/poi/{}/average-rating - Getting average rating", poiId);

                return poiReviewService.getAverageRatingByPoiId(poiId)
                                .map(avgRating -> ResponseEntity.ok(avgRating));
        }

        @GetMapping("/poi/{poi_id}/count")
        @Operation(summary = "Count reviews for a POI", description = "Returns the total number of reviews for a specific POI")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Review count retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class, example = "42")))
        })
        public Mono<ResponseEntity<Long>> getReviewCountByPoiId(
                        @Parameter(description = "Unique ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.info("GET /api-reviews/poi/{}/count - Getting review count", poiId);

                return poiReviewService.getReviewCountByPoiId(poiId)
                                .map(count -> ResponseEntity.ok(count));
        }

        @PatchMapping("/{review_id}/like")
        @Operation(summary = "Like a review", description = "Increments the like counter for a review")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Like added successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        public Mono<ResponseEntity<PoiReviewDTO>> incrementLikes(
                        @Parameter(description = "Unique ID of the review", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("review_id") UUID reviewId) {
                log.info("PATCH /api-reviews/{}/like - Incrementing likes", reviewId);

                return poiReviewService.incrementLikes(reviewId)
                                .map(review -> ResponseEntity.ok(review))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @PatchMapping("/{review_id}/unlike")
        @Operation(summary = "Unlike a review", description = "Increments the unlike counter for a review")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Unlike added successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        public Mono<ResponseEntity<PoiReviewDTO>> incrementDislikes(
                        @Parameter(description = "Unique ID of the review", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("review_id") UUID reviewId) {
                log.info("PATCH /api-reviews/{}/unlike - Incrementing unlikes", reviewId);

                return poiReviewService.incrementDislikes(reviewId)
                                .map(review -> ResponseEntity.ok(review))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @GetMapping("/poi/{poi_id}/stats")
        @Operation(summary = "Get POI review statistics", description = "Retrieves complete review statistics for a POI (average rating and review count)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                                        {
                                            "averageRating": 4.2,
                                            "reviewCount": 42
                                        }
                                        """)))
        })
        public Mono<ResponseEntity<Object>> getPoiReviewStats(
                        @Parameter(description = "Unique ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.info("GET /api-reviews/poi/{}/stats - Getting review statistics", poiId);

                return Mono.zip(
                                poiReviewService.getAverageRatingByPoiId(poiId),
                                poiReviewService.getReviewCountByPoiId(poiId))
                                .map(tuple -> {
                                        var stats = new Object() {
                                                public final Double averageRating = tuple.getT1();
                                                public final Long reviewCount = tuple.getT2();
                                        };
                                        return ResponseEntity.ok((Object) stats);
                                });
        }
}