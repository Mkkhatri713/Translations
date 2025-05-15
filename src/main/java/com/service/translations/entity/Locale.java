package com.service.translations.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locale")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Locale {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., "en", "fr", "hi"
}
