package com.service.translations.util;

import com.service.translations.entity.Locale;
import com.service.translations.entity.Tag;
import com.service.translations.entity.Translation;
import com.service.translations.repository.LocaleRepository;
import com.service.translations.repository.TagRepository;
import com.service.translations.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TranslationRepository translationRepository;
    private final LocaleRepository localeRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Skip if translations already exist
        if (translationRepository.count() > 0) {
            System.out.println("Skipping data seeding...");
            return;
        }

        System.out.println("Seeding locales and tags...");

        // Step 1: Ensure Locales exist
        String[] localeCodes = {"en", "fr", "es", "de"};
        Map<String, Locale> localeMap = new HashMap<>();
        for (String code : localeCodes) {
            Locale locale = localeRepository.findByCode(code)
                    .orElseGet(() -> localeRepository.save(new Locale(null, code)));
            localeMap.put(code, locale);
        }

        // Step 2: Ensure Tags exist
        String[] tagNames = {"mobile", "desktop", "web", "api"};
        Map<String, Tag> tagMap = new HashMap<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(null, tagName)));
            tagMap.put(tagName, tag);
        }

        System.out.println("Seeding translations...");

        // Step 3: Create Translations
        List<Translation> bulk = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 100_000; i++) {
            String localeCode = localeCodes[random.nextInt(localeCodes.length)];
            String tagName = tagNames[random.nextInt(tagNames.length)];

            Translation translation = Translation.builder()
                    .key("key_" + i)
                    .content("This is content number " + i)
                    .locale(localeMap.get(localeCode))
                    .tag(tagMap.get(tagName))
                    .build();

            bulk.add(translation);

            if (i % 1000 == 0) {
                translationRepository.saveAll(bulk);
                bulk.clear();
                System.out.println("Inserted " + i + " translations...");
            }
        }

        if (!bulk.isEmpty()) {
            translationRepository.saveAll(bulk);
        }

        System.out.println("Seeding complete.");
    }
}
