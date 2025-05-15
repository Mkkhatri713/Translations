package com.service.translations.service;

import com.service.translations.dto.FullTranslationExportDTO;
import com.service.translations.dto.TranslationExportDTO;
import com.service.translations.dto.TranslationRequestDTO;
import com.service.translations.entity.Translation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface    TranslationService {
    Translation create(TranslationRequestDTO req);
    Page<Translation> search(String key, String tag, String content, Pageable pageable);
//    List<TranslationExportDto> exportJson(String locale);

//    @Cacheable("allTranslations")
//    ConcurrentMap<String, ConcurrentMap<String, String>> exportAll();

//    Page<TranslationExportDTO> exportJson(String locale, Pageable pageable);

    Page<TranslationExportDTO> exportJson(String locale, String tag, Pageable pageable);

    @Cacheable(value = "allTranslations", key = "{#pageable.pageNumber,#pageable.pageSize}")
    Page<FullTranslationExportDTO> exportAll(Pageable pageable);

    Translation getById(Long id);

    @Transactional
    Translation update(Long id, TranslationRequestDTO req);

    @Transactional
    void delete(Long id);

    Map<String, String> exportBundle(String locale, String tag);
}

