package com.service.translations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequestDTO {

    @NotBlank(message = "Key must not be blank")
    private String key;

    @NotNull(message = "Locale ID is required")
    private Long localeId;

    @NotBlank(message = "Content must not be blank")
    private String content;
    @NotNull(message = "tagId must not be blank")
    private Long tagId;
}
