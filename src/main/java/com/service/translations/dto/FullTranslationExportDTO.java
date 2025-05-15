package com.service.translations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FullTranslationExportDTO {
    private Long id;
    private String localeCode;
    private String key;
    private String content;
    private String tagName;
    private LocalDateTime updatedAt;
}
