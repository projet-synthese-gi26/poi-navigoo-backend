package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.blog.BlogService;
import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;
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
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Blogs", description = "API for managing blog posts related to Points of Interest")
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new blog post", description = "Creates a new blog post and associates it with a user and a POI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog post created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided", content = @Content)
    })
    public Mono<BlogDTO> createBlog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data for the new blog post to create", required = true, content = @Content(schema = @Schema(implementation = CreateBlogRequest.class))) @RequestBody CreateBlogRequest blogCreateDto) {
        log.info("REST request to create blog post: {}", blogCreateDto.getTitle());
        return blogService.createBlog(blogCreateDto);
    }

    @PutMapping("/{blog_id}")
    @Operation(summary = "Update an existing blog post", description = "Updates the details of an existing blog post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog post updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class))),
            @ApiResponse(responseCode = "404", description = "Blog post not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public Mono<BlogDTO> updateBlog(
            @Parameter(description = "ID of the blog post to update", required = true) @PathVariable UUID blog_id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated data for the blog post", required = true, content = @Content(schema = @Schema(implementation = UpdateBlogRequest.class))) @RequestBody UpdateBlogRequest blogUpdateDto) {
        log.info("REST request to update blog post: {}", blog_id);
        return blogService.updateBlog(blog_id, blogUpdateDto);
    }

    @GetMapping("/{blog_id}")
    @Operation(summary = "Get a blog post by ID", description = "Retrieves a single blog post by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog post found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class))),
            @ApiResponse(responseCode = "404", description = "Blog post not found", content = @Content)
    })
    public Mono<BlogDTO> getBlogById(
            @Parameter(description = "Unique ID of the blog post", required = true) @PathVariable UUID blog_id) {
        log.debug("REST request to get blog post by ID: {}", blog_id);
        return blogService.getBlogById(blog_id);
    }

    @GetMapping
    @Operation(summary = "Get all blog posts", description = "Retrieves a list of all blog posts in the system.")
    @ApiResponse(responseCode = "200", description = "List of all blog posts", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class)))
    public Flux<BlogDTO> getAllBlogs() {
        log.debug("REST request to get all blog posts");
        return blogService.getAllBlogs();
    }

    @GetMapping("/user/{user_id}")
    @Operation(summary = "Get blog posts by user ID", description = "Retrieves all blog posts written by a specific user.")
    @ApiResponse(responseCode = "200", description = "List of blog posts for the user", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class)))
    public Flux<BlogDTO> getBlogsByUserId(
            @Parameter(description = "ID of the user who wrote the blog posts", required = true) @PathVariable UUID user_id) {
        log.debug("REST request to get blog posts for user: {}", user_id);
        return blogService.getBlogsByUserId(user_id);
    }

    @GetMapping("/poi/{poi_id}")
    @Operation(summary = "Get blog posts by POI ID", description = "Retrieves all blog posts associated with a specific Point of Interest.")
    @ApiResponse(responseCode = "200", description = "List of blog posts for the POI", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class)))
    public Flux<BlogDTO> getBlogsByPoiId(
            @Parameter(description = "ID of the Point of Interest the blog posts are associated with", required = true) @PathVariable UUID poi_id) {
        log.debug("REST request to get blog posts for POI: {}", poi_id);
        return blogService.getBlogsByPoiId(poi_id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search blog posts by title", description = "Searches for blog posts with a title containing the given search term (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "List of blog posts matching the title", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BlogDTO.class)))
    public Flux<BlogDTO> searchBlogsByTitle(
            @Parameter(description = "The search term to find in blog post titles", required = true, example = "travel") @RequestParam String title) {
        log.debug("REST request to search blog posts by title: {}", title);
        return blogService.searchBlogsByTitle(title);
    }

    @DeleteMapping("/{blog_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a blog post", description = "Permanently deletes a blog post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blog post deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Blog post not found", content = @Content)
    })
    public Mono<Void> deleteBlog(
            @Parameter(description = "ID of the blog post to delete", required = true) @PathVariable UUID blog_id) {
        log.info("REST request to delete blog post: {}", blog_id);
        return blogService.deleteBlog(blog_id);
    }

    /*
     * @GetMapping("/count/user/{userId}")
     * 
     * @Operation(summary = "Count blog posts by user", description =
     * "Gets the total number of blog posts written by a specific user.")
     * 
     * @ApiResponse(responseCode = "200", description =
     * "Total number of blog posts for the user", content = @Content(schema
     * = @Schema(implementation = Long.class, example = "7")))
     * public Mono<Long> countBlogsByUserId(
     * 
     * @Parameter(description = "ID of the user", required = true) @PathVariable
     * UUID userId) {
     * log.debug("REST request to count blog posts for user: {}", userId);
     * return blogService.countBlogsByUserId(userId);
     * }
     * 
     * @GetMapping("/count/poi/{poiId}")
     * 
     * @Operation(summary = "Count blog posts by POI", description =
     * "Gets the total number of blog posts associated with a specific POI.")
     * 
     * @ApiResponse(responseCode = "200", description =
     * "Total number of blog posts for the POI", content = @Content(schema
     * = @Schema(implementation = Long.class, example = "3")))
     * public Mono<Long> countBlogsByPoiId(
     * 
     * @Parameter(description = "ID of the POI", required = true) @PathVariable UUID
     * poiId) {
     * log.debug("REST request to count blog posts for POI: {}", poiId);
     * return blogService.countBlogsByPoiId(poiId);
     * }
     */
}