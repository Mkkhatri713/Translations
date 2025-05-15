package com.service.translations.serviceImplementation;

import com.service.translations.dto.FullTranslationExportDTO;
import com.service.translations.dto.TranslationExportDTO;
import com.service.translations.dto.TranslationRequestDTO;
import com.service.translations.entity.Locale;
import com.service.translations.entity.Tag;
import com.service.translations.entity.Translation;
import com.service.translations.exception.CustomException;
import com.service.translations.repository.LocaleRepository;
import com.service.translations.repository.TagRepository;
import com.service.translations.repository.TranslationRepository;
import com.service.translations.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository repo;

    private final LocaleRepository localeRepository;
    private final TagRepository tagRepository;

    @Override
    public Translation create(TranslationRequestDTO req) {
        Locale locale = localeRepository.findById(req.getLocaleId())
                .orElseThrow(() -> new CustomException("Locale not found"));

        Tag tag = tagRepository.findById(req.getTagId())
                .orElseThrow(() -> new CustomException("tag not found"));

        boolean exists = repo.existsByKeyIgnoreCaseAndLocaleAndTag(req.getKey(), locale, tag);
        if (exists) {
            throw new CustomException("Duplicate translation key for the selected locale and tag");
        }
        String normalizedKey = req.getKey().trim().toLowerCase();
        Translation t = Translation.builder()
                .key(normalizedKey)
                .content(req.getContent())
                .locale(locale)
                .tag(tag)
                .build();

        return repo.save(t);
    }

    @Override
    public Page<Translation> search(String key, String tag, String content, Pageable pageable) {
        Specification<Translation> spec = Specification.where(null);

        if (key != null && !key.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("key")), key.toLowerCase()));
        }

        if (tag != null && !tag.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.join("tag").get("name")), tag.toLowerCase()));
        }

        if (content != null && !content.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("content")), content.toLowerCase()));
        }

        return repo.findAll(spec, pageable);
    }


//    @Override
//    public Page<TranslationExportDTO> exportJson(String locale, Pageable pageable) {
//        Page<TranslationExportDTO> page = repo.findAllByLocaleCode(locale, pageable);
//        if (page.isEmpty()) {
//            throw new CustomException("No translations found for locale: " + locale);
//        }
//        return page;
//    }


    @Override
    public Page<TranslationExportDTO> exportJson(String locale, String tagName, Pageable pageable) {
        Page<TranslationExportDTO> page;

        if (tagName != null && !tagName.isEmpty()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseThrow(() -> new CustomException("Tag not found: " + tagName));
            page = repo.findAllByLocaleCodeAndTag(locale, tag, pageable); // now passing Tag entity
        } else {
            page = repo.findAllByLocaleCode(locale, pageable);
        }

        if (page.isEmpty()) {
            throw new CustomException("No translations found for locale: " + locale + (tagName != null ? " and tag: " + tagName : ""));
        }

        return page;
    }


    public Map<String, String> exportBundle(String locale, String tag) {
        Pageable all = Pageable.unpaged(); // no pagination, fetch all
        Page<TranslationExportDTO> page = exportJson(locale, tag, all);

        return page.getContent().stream()
                .collect(Collectors.toMap(TranslationExportDTO::getKey, TranslationExportDTO::getContent));
    }


    // In TranslationServiceImpl.java
    @Override
    @Cacheable(value = "allTranslations", key = "{#pageable.pageNumber,#pageable.pageSize}",
            unless = "#result.getContent().size() == 0")
    public Page<FullTranslationExportDTO> exportAll(Pageable pageable) {
        return repo.findAllForExport(pageable);
    }

//    @Override
//    @Cacheable(value = "localeTranslations", key = "{#locale,#pageable.pageNumber,#pageable.pageSize}",
//            unless = "#result.getContent().size() == 0")
//    public Page<TranslationExportDTO> exportJson(String locale, Pageable pageable) {
//        Page<TranslationExportDTO> page = repo.findBatchByLocaleCode(locale, pageable);
//        if (page.isEmpty()) {
//            throw new CustomException("No translations found for locale: " + locale);
//        }
//        return page;
//    }
    @Override
    public Translation getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new CustomException("Translation not found with ID: " + id));
    }

    @Override
    @Transactional
    public Translation update(Long id, TranslationRequestDTO req) {
        Translation existing = repo.findById(id)
                .orElseThrow(() -> new CustomException("Translation not found with ID: " + id));

        Locale locale = localeRepository.findById(req.getLocaleId())
                .orElseThrow(() -> new CustomException("Locale not found"));

        Tag tag = tagRepository.findById(req.getTagId())
                .orElseThrow(() -> new CustomException("Tag not found"));

        String normalizedKey = req.getKey().trim().toLowerCase();

        boolean duplicateExists = repo.existsByKeyIgnoreCaseAndLocaleAndTagAndIdNot(normalizedKey, locale, tag, id);
        if (duplicateExists) {
            throw new CustomException("Duplicate translation key for the selected locale and tag");
        }

        existing.setKey(normalizedKey);
        existing.setContent(req.getContent());
        existing.setLocale(locale);
        existing.setTag(tag);

        return repo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Translation translation = repo.findById(id)
                .orElseThrow(() -> new CustomException("Translation not found with ID: " + id));

        repo.delete(translation);
    }



}
