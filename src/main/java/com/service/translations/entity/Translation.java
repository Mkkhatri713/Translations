package com.service.translations.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "translations", indexes = {
        @Index(name = "idx_translation_key", columnList = "key"),
        @Index(name = "idx_translation_content", columnList = "content"),
        @Index(name = "idx_locale_updated", columnList = "locale_id,updatedAt"), // Add this
        @Index(name = "idx_export_all", columnList = "updatedAt") // Add this
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Translation {

    @Id @GeneratedValue
    private Long id;

    @NotBlank
    @Column(name = "`key`")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locale_id", nullable = false)
    private Locale locale;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}