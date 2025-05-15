package com.service.translations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tag DTO for create/update operations")
public class TagRequestDTO {

    @Schema(description = "ID of the tag", example = "1")
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Schema(description = "Tag name (e.g., mobile, web)", example = "mobile", required = true)
    private String name;
}
