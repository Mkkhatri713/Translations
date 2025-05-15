package com.service.translations.controller;

import com.service.translations.dto.TagRequestDTO;
import com.service.translations.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
@Validated
@Tag(name = "Tag", description = "APIs for managing tags")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @Operation(
            summary = "Create a new tag",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tag created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Tag already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<TagRequestDTO> create(@RequestBody @Valid TagRequestDTO request) {
        return ResponseEntity.status(201).body(tagService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing tag",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Tag not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<TagRequestDTO> update(
            @Parameter(description = "ID of the tag to update") @PathVariable Long id,
            @RequestBody @Valid TagRequestDTO request) {
        return ResponseEntity.ok(tagService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a tag by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Tag not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Void> delete(@Parameter(description = "ID of the tag to delete") @PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a tag by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Tag not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<TagRequestDTO> get(@Parameter(description = "ID of the tag to fetch") @PathVariable Long id) {
        return ResponseEntity.ok(tagService.get(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all tags",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<TagRequestDTO>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }
}
