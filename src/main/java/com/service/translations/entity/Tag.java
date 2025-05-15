    package com.service.translations.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import lombok.*;

    import javax.persistence.*;

    @Entity
    @Table(name = "tags")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    public class Tag {
        @Id
        @GeneratedValue
        private Long id;

        @Column(unique = true, nullable = false)
        private String name; // e.g., "mobile", "web"
    }

