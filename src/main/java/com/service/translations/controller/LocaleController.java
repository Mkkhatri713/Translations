package com.service.translations.controller;

import com.service.translations.dto.LocaleRequestDTO;
import com.service.translations.service.LocaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locales")
@Validated
@Tag(name = "Locale", description = "APIs for managing locales")
public class LocaleController {

    private final LocaleService localeService;

    @PostMapping
    @Operation(
            summary = "Create a new locale",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Locale created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Locale already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<LocaleRequestDTO> create(@RequestBody @Valid LocaleRequestDTO request) {
        return ResponseEntity.status(201).body(localeService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing locale",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locale updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Locale not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<LocaleRequestDTO> update(
            @Parameter(description = "ID of the locale to update") @PathVariable Long id,
            @RequestBody @Valid LocaleRequestDTO request) {
        return ResponseEntity.ok(localeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a locale by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Locale deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Locale not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Void> delete(@Parameter(description = "ID of the locale to delete") @PathVariable Long id) {
        localeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a locale by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locale retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Locale not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<LocaleRequestDTO> get(@Parameter(description = "ID of the locale to fetch") @PathVariable Long id) {
        return ResponseEntity.ok(localeService.get(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all locales",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locales retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<LocaleRequestDTO>> getAll() {
        return ResponseEntity.ok(localeService.getAll());
    }
}
