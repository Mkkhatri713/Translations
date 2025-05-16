package com.service.translations.repository;

import com.service.translations.dto.FullTranslationExportDTO;
import com.service.translations.dto.TranslationExportDTO;
import com.service.translations.entity.Locale;
import com.service.translations.entity.Tag;
import com.service.translations.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long>, JpaSpecificationExecutor<Translation> {

    List<Translation> findByTag(String tag);

    List<Translation> findByLocaleAndKey(String locale, String key);

    List<Translation> findByContentContainingIgnoreCase(String keyword);

    List<Translation> findByUpdatedAtAfter(LocalDateTime updatedAt);

    Stream<Translation> streamAllByLocale(String locale);

    List<Translation> findByLocale_Code(String localeCode);

    @Query(
            value = "SELECT new com.service.translations.dto.TranslationExportDTO(t.key, t.content) " +
                    "FROM Translation t WHERE t.locale.code = :locale ORDER BY t.updatedAt DESC",
            countQuery = "SELECT count(t) FROM Translation t WHERE t.locale.code = :locale"
    )
    Page<TranslationExportDTO> findAllByLocaleCode(@Param("locale") String locale, Pageable pageable);


    @Query(
            value = "SELECT new com.service.translations.dto.TranslationExportDTO(t.key, t.content) " +
                    "FROM Translation t WHERE t.locale.code = :locale AND t.tag = :tag ORDER BY t.updatedAt DESC",
            countQuery = "SELECT count(t) FROM Translation t WHERE t.locale.code = :locale AND t.tag = :tag"
    )
    Page<TranslationExportDTO> findAllByLocaleCodeAndTag(@Param("locale") String locale, @Param("tag") Tag tag, Pageable pageable);

    // Add this to TranslationRepository.java
    boolean existsByKeyIgnoreCaseAndLocaleAndTag(String key, Locale locale, Tag tag);

    boolean existsByKeyIgnoreCaseAndLocaleAndTagAndIdNot(String key, Locale locale, Tag tag, Long id);

//    @Query("""
//    SELECT new com.service.translations.dto.FullTranslationExportDTO(
//        t.id,t.locale.code, t.key, t.content, t.tag.name, t.updatedAt
//    )
//    FROM Translation t
//    ORDER BY t.updatedAt DESC
//""")
//    @QueryHints({
//            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
//            @QueryHint(name = "org.hibernate.fetchSize", value = "500")
//    })
//    Page<FullTranslationExportDTO> findAllForExport(Pageable pageable);

    // In TranslationRepository.java
    @Query("""
SELECT new com.service.translations.dto.FullTranslationExportDTO(
    t.id, t.locale.code, t.key, t.content, t.tag.name, t.updatedAt
)
FROM Translation t
LEFT JOIN t.locale
LEFT JOIN t.tag
ORDER BY t.updatedAt DESC
""")
    @QueryHints({
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name = "org.hibernate.fetchSize", value = "500"),
            @QueryHint(name = "org.hibernate.comment", value = "custom_export_query")
    })
    Page<FullTranslationExportDTO> findAllForExport(Pageable pageable);
}