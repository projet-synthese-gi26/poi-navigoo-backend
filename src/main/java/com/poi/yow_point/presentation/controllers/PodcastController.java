package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.podcast.PodcastService;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/podcasts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Podcasts", description = "API for managing podcasts related to Points of Interest")
public class PodcastController {

    private final PodcastService podcastService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new podcast", description = "Creates a new podcast and associates it with a user and a POI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Podcast created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided", content = @Content)
    })
    public Mono<PodcastDTO> createPodcast(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data for the new podcast to create", required = true, content = @Content(schema = @Schema(implementation = PodcastCreateRequest.class))) @RequestBody PodcastCreateRequest podcastCreateDto) {
        log.info("REST request to create podcast: {}", podcastCreateDto.getTitle());
        return podcastService.createPodcast(podcastCreateDto);
    }

    @PutMapping("/{podcast_id}")
    @Operation(summary = "Update an existing podcast", description = "Updates the details of an existing podcast by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Podcast updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class))),
            @ApiResponse(responseCode = "404", description = "Podcast not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public Mono<PodcastDTO> updatePodcast(
            @Parameter(description = "ID of the podcast to update", required = true) @PathVariable UUID podcast_id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated data for the podcast", required = true, content = @Content(schema = @Schema(implementation = UpdatePodcastRequest.class))) @RequestBody UpdatePodcastRequest podcastUpdateDto) {
        log.info("REST request to update podcast: {}", podcast_id);
        return podcastService.updatePodcast(podcast_id, podcastUpdateDto);
    }

    @GetMapping("/{podcast_id}")
    @Operation(summary = "Get a podcast by ID", description = "Retrieves a single podcast by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Podcast found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class))),
            @ApiResponse(responseCode = "404", description = "Podcast not found", content = @Content)
    })
    public Mono<PodcastDTO> getPodcastById(
            @Parameter(description = "Unique ID of the podcast", required = true) @PathVariable UUID podcast_id) {
        log.debug("REST request to get podcast by ID: {}", podcast_id);
        return podcastService.getPodcastById(podcast_id);
    }

    @GetMapping
    @Operation(summary = "Get all podcasts", description = "Retrieves a list of all podcasts in the system.")
    @ApiResponse(responseCode = "200", description = "List of all podcasts", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class)))
    public Flux<PodcastDTO> getAllPodcasts() {
        log.debug("REST request to get all podcasts");
        return podcastService.getAllPodcasts();
    }

    @GetMapping("/user/{user_id}")
    @Operation(summary = "Get podcasts by user ID", description = "Retrieves all podcasts created by a specific user.")
    @ApiResponse(responseCode = "200", description = "List of podcasts for the user", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class)))
    public Flux<PodcastDTO> getPodcastsByUserId(
            @Parameter(description = "ID of the user who created the podcasts", required = true) @PathVariable UUID user_id) {
        log.debug("REST request to get podcasts for user: {}", user_id);
        return podcastService.getPodcastsByUserId(user_id);
    }

    @GetMapping("/poi/{poi_id}")
    @Operation(summary = "Get podcasts by POI ID", description = "Retrieves all podcasts associated with a specific Point of Interest.")
    @ApiResponse(responseCode = "200", description = "List of podcasts for the POI", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class)))
    public Flux<PodcastDTO> getPodcastsByPoiId(
            @Parameter(description = "ID of the Point of Interest the podcasts are associated with", required = true) @PathVariable("poi_id") UUID poiId) {
        log.debug("REST request to get podcasts for POI: {}", poiId);
        return podcastService.getPodcastsByPoiId(poiId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search podcasts by title", description = "Searches for podcasts with a title containing the given search term (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "List of podcasts matching the title", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class)))
    public Flux<PodcastDTO> searchPodcastsByTitle(
            @Parameter(description = "The search term to find in podcast titles", required = true, example = "history") @RequestParam String title) {
        log.debug("REST request to search podcasts by title: {}", title);
        return podcastService.searchPodcastsByTitle(title);
    }

    @GetMapping("/duration")
    @Operation(summary = "Get podcasts by duration range", description = "Retrieves podcasts with a duration (in seconds) within the specified range.")
    @ApiResponse(responseCode = "200", description = "List of podcasts within the duration range", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PodcastDTO.class)))
    public Flux<PodcastDTO> getPodcastsByDurationRange(
            @Parameter(description = "Minimum duration in seconds", required = true, example = "60") @RequestParam Integer minDuration,
            @Parameter(description = "Maximum duration in seconds", required = true, example = "300") @RequestParam Integer maxDuration) {
        log.debug("REST request to get podcasts by duration range: {}-{}", minDuration, maxDuration);
        return podcastService.getPodcastsByDurationRange(minDuration, maxDuration);
    }

    @DeleteMapping("/{podcast_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a podcast", description = "Permanently deletes a podcast by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Podcast deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Podcast not found", content = @Content)
    })
    public Mono<Void> deletePodcast(
            @Parameter(description = "ID of the podcast to delete", required = true) @PathVariable("podcast_id") UUID podcastId) {
        log.info("REST request to delete podcast: {}", podcastId);
        return podcastService.deletePodcast(podcastId);
    }

    /*
     * @GetMapping("/count/user/{user_id}")
     * 
     * @Operation(summary = "Count podcasts by user", description =
     * "Gets the total number of podcasts created by a specific user.")
     * 
     * @ApiResponse(responseCode = "200", description =
     * "Total number of podcasts for the user", content = @Content(schema
     * = @Schema(implementation = Long.class, example = "5")))
     * public Mono<Long> countPodcastsByUserId(
     * 
     * @Parameter(description = "ID of the user", required = true) @PathVariable
     * UUID userId) {
     * log.debug("REST request to count podcasts for user: {}", userId);
     * return podcastService.countPodcastsByUserId(userId);
     * }
     * 
     * @GetMapping("/count/poi/{poi_id}")
     * 
     * @Operation(summary = "Count podcasts by POI", description =
     * "Gets the total number of podcasts associated with a specific POI.")
     * 
     * @ApiResponse(responseCode = "200", description =
     * "Total number of podcasts for the POI", content = @Content(schema
     * = @Schema(implementation = Long.class, example = "12")))
     * public Mono<Long> countPodcastsByPoiId(
     * 
     * @Parameter(description = "ID of the POI", required = true) @PathVariable UUID
     * poiId) {
     * log.debug("REST request to count podcasts for POI: {}", poiId);
     * return podcastService.countPodcastsByPoiId(poiId);
     * }
     */
}