    package com.service.translations.controller;

    import com.service.translations.dto.FullTranslationExportDTO;
    import com.service.translations.dto.TranslationExportDTO;
    import com.service.translations.dto.TranslationRequestDTO;
    import com.service.translations.entity.Translation;
    import com.service.translations.exception.ErrorResponse;
    import com.service.translations.service.TranslationService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;

    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.CacheControl;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;


    import javax.servlet.http.HttpServletResponse;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.concurrent.CompletableFuture;
    import java.util.concurrent.TimeUnit;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/translations")
    @RequiredArgsConstructor
    @Tag(name = "Translation API", description = "API for managing multilingual translations with context tags")
    public class TranslationController {

        private final TranslationService service;

        @Operation(summary = "Create a new translation")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Created successfully"),
                @ApiResponse(responseCode = "400", description = "Validation error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<Translation> create(
                @Validated @RequestBody TranslationRequestDTO req
        ) {
            return ResponseEntity.status(201).body(service.create(req));
        }

        @Operation(summary = "Search translations by key, tag, or content")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                @ApiResponse(responseCode = "400", description = "Bad Request - Invalid query parameters"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
                @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        @GetMapping("/search")
        public ResponseEntity<Page<Translation>> search(
                @Parameter(description = "Search by translation key")
                @RequestParam(required = false) String key,

                @Parameter(description = "Search by context tag (e.g., mobile, desktop)")
                @RequestParam(required = false) String tag,

                @Parameter(description = "Search by translation content")
                @RequestParam(required = false) String content,

                @Parameter(description = "Page number (starting from 0)", required = true)
                @RequestParam int page,

                @Parameter(description = "Number of records per page", required = true)
                @RequestParam int size
        ) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            Page<Translation> result = service.search(key, tag, content, pageable);
            return ResponseEntity.ok(result);
        }

        @Operation(summary = "get all transactions, endpoint for fetching huge data ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "All translations exported successfully"),
                @ApiResponse(responseCode = "404", description = "No translations found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        // In TranslationController.java
        @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
        public CompletableFuture<ResponseEntity<Map<String, Object>>> exportAll(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10000") int size) {

            return CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                Pageable pageable = PageRequest.of(page, size);
                Page<FullTranslationExportDTO> resultPage = service.exportAll(pageable);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("translations", resultPage.getContent());
                responseBody.put("currentPage", resultPage.getNumber());
                responseBody.put("totalItems", resultPage.getTotalElements());
                responseBody.put("totalPages", resultPage.getTotalPages());

                long duration = System.currentTimeMillis() - startTime;
                responseBody.put("processingTimeMs", duration);

                return ResponseEntity.ok()
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                        .body(responseBody);
            });
        }
        @Operation(summary = "Export translations in JSON format for locale and tag")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "JSON export returned successfully"),
                @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid locale"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
                @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        @GetMapping(value = "/exportByLocalAndTag", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Map<String, Object>> export(
                @RequestParam String locale,
                @RequestParam(required = false) String tag,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {

            long startTime = System.currentTimeMillis();

            Pageable pageable = PageRequest.of(page, size);
            Page<TranslationExportDTO> resultPage = service.exportJson(locale, tag, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultPage.getContent().stream()
                    .collect(Collectors.toMap(TranslationExportDTO::getKey, TranslationExportDTO::getContent)));
            response.put("currentPage", resultPage.getNumber());
            response.put("totalItems", resultPage.getTotalElements());
            response.put("totalPages", resultPage.getTotalPages());
            response.put("processingTimeMs", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .body(response);
        }
        @Operation(summary = "Export translations in JSON format to supply translations for frontend applications like\n" +
                " Vue.js ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "JSON export returned successfully"),
                @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid locale"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
                @ApiResponse(responseCode = "403", description = "Forbidden - Access denied"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        @GetMapping(value = "/export/{locale}.json", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Map<String, String>> exportFull(
                @PathVariable String locale,
                @RequestParam(required = false) String tag) {

            Map<String, String> translations = service.exportBundle(locale, tag);
            if (translations.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(6, TimeUnit.HOURS))
                    .body(translations);
        }


        @Operation(summary = "Get translation by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Translation fetched successfully"),
                @ApiResponse(responseCode = "404", description = "Translation not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<Translation> getById(
                @Parameter(description = "ID of the translation to retrieve", required = true)
                @PathVariable Long id) {
            return ResponseEntity.ok(service.getById(id));
        }

        @Operation(summary = "Update an existing translation by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Translation updated successfully"),
                @ApiResponse(responseCode = "400", description = "Validation error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "404", description = "Translation not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        public ResponseEntity<Translation> update(
                @Parameter(description = "ID of the translation to update", required = true)
                @PathVariable Long id,
                @Validated @RequestBody TranslationRequestDTO req) {
            return ResponseEntity.ok(service.update(id, req));
        }

        @Operation(summary = "Delete translation by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Translation deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Translation not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(
                @Parameter(description = "ID of the translation to delete", required = true)
                @PathVariable Long id) {
            service.delete(id);
            return ResponseEntity.ok("Translation deleted successfully");
        }


    }
