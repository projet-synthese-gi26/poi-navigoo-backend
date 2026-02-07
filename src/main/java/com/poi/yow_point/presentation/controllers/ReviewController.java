package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.poiReview.ReviewService;
import com.poi.yow_point.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api-review") // Changed from /api-reviews to /api-review per user request for post routes? or mix? existing was /api-reviews. User asked for /api-review/{id}. I will map /api-review mainly.
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Reviews", description = "API for managing reviews on POIs, Blogs, and Podcasts")
public class ReviewController {

    private final ReviewService reviewService;

    // --- Create Reviews ---

    @PostMapping("/poi/{poi_id}")
    @Operation(summary = "Create review for POI", description = "Creates a new review for a Point of Interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PoiReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    public Mono<ResponseEntity<Object>> createReviewForPoi(
            @PathVariable("poi_id") UUID poiId,
            @RequestBody ReviewRequestDTO reviewDTO) {
        log.info("POST /api-review/poi/{} - Creating review", poiId);
        return reviewService.createReviewForPoi(poiId, reviewDTO)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body((Object) created))
                .onErrorResume(e -> {
                    log.error("Error creating review for POI: {}", poiId, e);
                    return Mono.just(ResponseEntity.badRequest().body("Error creating review: " + e.getMessage()));
                });
    }

    @PostMapping("/blog/{blog_id}")
    @Operation(summary = "Create review for Blog", description = "Creates a new review for a Blog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    public Mono<ResponseEntity<Object>> createReviewForBlog(
            @PathVariable("blog_id") UUID blogId,
            @RequestBody ReviewRequestDTO reviewDTO) {
        log.info("POST /api-review/blog/{} - Creating review", blogId);
        return reviewService.createReviewForBlog(blogId, reviewDTO)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body((Object) created))
                .onErrorResume(e -> {
                    log.error("Error creating review for Blog: {}", blogId, e);
                    return Mono.just(ResponseEntity.badRequest().body("Error creating review: " + e.getMessage()));
                });
    }

    @PostMapping("/podcast/{podcast_id}")
    @Operation(summary = "Create review for Podcast", description = "Creates a new review for a Podcast")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    public Mono<ResponseEntity<Object>> createReviewForPodcast(
            @PathVariable("podcast_id") UUID podcastId,
            @RequestBody ReviewRequestDTO reviewDTO) {
        log.info("POST /api-review/podcast/{} - Creating review", podcastId);
        return reviewService.createReviewForPodcast(podcastId, reviewDTO)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body((Object) created))
                .onErrorResume(e -> {
                    log.error("Error creating review for Podcast: {}", podcastId, e);
                    return Mono.just(ResponseEntity.badRequest().body("Error creating review: " + e.getMessage()));
                });
    }

    // --- Get Reviews ---

    @GetMapping("/{review_id}")
    @Operation(summary = "Get review by ID", description = "Retrieves a review by its unique ID")
    public Mono<ResponseEntity<Object>> getReviewById(@PathVariable("review_id") UUID reviewId) {
        log.info("GET /api-review/{} - Fetching review", reviewId);
        return reviewService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/poi/{poi_id}")
    @Operation(summary = "Get reviews for POI", description = "Retrieves all reviews for a specific POI")
    public Flux<PoiReviewResponseDTO> getReviewsByPoiId(@PathVariable("poi_id") UUID poiId) {
        log.info("GET /api-review/poi/{} - Fetching reviews", poiId);
        return reviewService.getReviewsByPoiId(poiId);
    }

    @GetMapping("/blog/{blog_id}")
    @Operation(summary = "Get reviews for Blog", description = "Retrieves all reviews for a specific Blog")
    public Flux<BlogReviewResponseDTO> getReviewsByBlogId(@PathVariable("blog_id") UUID blogId) {
        log.info("GET /api-review/blog/{} - Fetching reviews", blogId);
        return reviewService.getReviewsByBlogId(blogId);
    }

    @GetMapping("/podcast/{podcast_id}")
    @Operation(summary = "Get reviews for Podcast", description = "Retrieves all reviews for a specific Podcast")
    public Flux<PodcastReviewResponseDTO> getReviewsByPodcastId(@PathVariable("podcast_id") UUID podcastId) {
        log.info("GET /api-review/podcast/{} - Fetching reviews", podcastId);
        return reviewService.getReviewsByPodcastId(podcastId);
    }

    @GetMapping("/user/{user_id}")
    @Operation(summary = "Get reviews by User", description = "Retrieves all reviews created by a user")
    public Flux<Object> getReviewsByUserId(@PathVariable("user_id") UUID userId) {
        log.info("GET /api-review/user/{} - Fetching reviews", userId);
        return reviewService.getReviewsByUserId(userId);
    }

    // --- Update/Delete/Stats ---

    @PutMapping("/{review_id}")
    @Operation(summary = "Update review", description = "Updates an existing review")
    public Mono<ResponseEntity<Object>> updateReview(
            @PathVariable("review_id") UUID reviewId,
            @RequestBody ReviewRequestDTO reviewDTO) {
        log.info("PUT /api-review/{} - Updating review", reviewId);
        return reviewService.updateReview(reviewId, reviewDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{review_id}")
    @Operation(summary = "Delete review", description = "Deletes a review by ID")
    public Mono<ResponseEntity<Void>> deleteReview(@PathVariable("review_id") UUID reviewId) {
        log.info("DELETE /api-review/{} - Deleting review", reviewId);
        return reviewService.deleteReview(reviewId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    // --- Specific Actions ---

    @PatchMapping("/{review_id}/like")
    public Mono<ResponseEntity<Object>> incrementLikes(@PathVariable("review_id") UUID reviewId) {
        return reviewService.incrementLikes(reviewId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{review_id}/unlike")
    public Mono<ResponseEntity<Object>> incrementDislikes(@PathVariable("review_id") UUID reviewId) {
        return reviewService.incrementDislikes(reviewId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}