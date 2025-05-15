package com.service.translations.dto;

import lombok.*;
import lombok.Builder;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Locale DTO for create/update operations")
public class LocaleRequestDTO {

    @Schema(description = "ID of the locale", example = "1")
    private Long id;

    @NotBlank(message = "Locale code is required")
    @Schema(description = "Locale code (e.g., en, fr)", example = "en", required = true)
    private String code;
}
