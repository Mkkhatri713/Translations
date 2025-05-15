package com.service.translations.tesController;

import com.service.translations.controller.TranslationController;
import com.service.translations.dto.FullTranslationExportDTO;
import com.service.translations.dto.TranslationExportDTO;
import com.service.translations.dto.TranslationRequestDTO;
import com.service.translations.entity.Translation;
import com.service.translations.exception.CustomException;
import com.service.translations.repository.TranslationRepository;
import com.service.translations.service.TranslationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationControllerTest {

    private static final Logger log = LoggerFactory.getLogger(TranslationControllerTest.class);

    @Mock
    private TranslationService service;

    @Mock
    private TranslationRepository repo;

    @InjectMocks
    private TranslationController controller;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final String LOCALE_EN = "en";
    private static final String LOCALE_FR = "fr";
    private static final String TAG_GENERAL = "general";
    private static final String TAG_MOBILE = "mobile";
    private static final String KEY_HELLO = "hello";
    private static final String KEY_WELCOME = "welcome";
    private static final String CONTENT_HELLO = "Hello World";
    private static final String CONTENT_WELCOME = "Welcome";

    @Nested
    @DisplayName("Create Translation")
    class CreateTranslationTests {
        @Test
        @DisplayName("Should return 201 and created translation when request is valid")
        void create_validRequest_returnsCreatedTranslation() {
            log.info("Running test: create_validRequest_returnsCreatedTranslation");
            TranslationRequestDTO request = createTranslationRequest(KEY_HELLO, 5L, CONTENT_HELLO, 4L);
            Translation expected = createTranslation(VALID_ID, KEY_HELLO, CONTENT_HELLO,
                    createLocale(5L, LOCALE_EN), createTag(4L, TAG_GENERAL));

            when(service.create(request)).thenReturn(expected);

            ResponseEntity<Translation> response = controller.create(request);

            assertAll(
                    () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals(KEY_HELLO, response.getBody().getKey()),
                    () -> assertEquals(CONTENT_HELLO, response.getBody().getContent()),
                    () -> assertEquals(LOCALE_EN, response.getBody().getLocale().getCode()),
                    () -> assertEquals(TAG_GENERAL, response.getBody().getTag().getName())
            );
            log.info("Translation created successfully: {}", response.getBody());
            verify(service).create(request);
        }
    }

    @Nested
    @DisplayName("Search Translations")
    class SearchTranslationsTests {
        @Test
        @DisplayName("Should return paginated results when searching by key, tag and content")
        void search_withAllParameters_returnsPaginatedResults() {
            log.info("Running test: search_withAllParameters_returnsPaginatedResults");
            Pageable pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());
            Page<Translation> expectedPage = new PageImpl<>(List.of(
                    createTranslation(VALID_ID, KEY_WELCOME, CONTENT_WELCOME,
                            createLocale(1L, LOCALE_EN), createTag(1L, TAG_MOBILE))
            ));

            when(service.search(KEY_WELCOME, TAG_MOBILE, CONTENT_WELCOME, pageable))
                    .thenReturn(expectedPage);

            ResponseEntity<Page<Translation>> response = controller.search(
                    KEY_WELCOME, TAG_MOBILE, CONTENT_WELCOME, 0, 10);

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals(1, response.getBody().getContent().size())
            );
            log.info("Search results: {}", response.getBody().getContent());
        }
    }

    @Nested
    @DisplayName("Export Translations")
    class ExportTranslationsTests {

        @Test
        @DisplayName("Should return translations map for given locale and tag")
        void export_validLocaleAndTag_returnsJsonMap() {
            log.info("Running test: export_validLocaleAndTag_returnsJsonMap");

            String locale = "en";
            String tag = "mobile";
            int page = 0;
            int size = 10;

            List<TranslationExportDTO> exportData = List.of(
                    createExportDto("greeting", "Hello"),
                    createExportDto("farewell", "Goodbye")
            );

            Page<TranslationExportDTO> mockPage = new PageImpl<>(exportData, PageRequest.of(page, size), exportData.size());

            when(service.exportJson(eq(locale), eq(tag), any(Pageable.class))).thenReturn(mockPage);

            ResponseEntity<Map<String, Object>> response = controller.export(locale, tag, page, size);

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertTrue(((Map<?, ?>) response.getBody().get("data")).containsKey("greeting")),
                    () -> assertEquals("Hello", ((Map<?, ?>) response.getBody().get("data")).get("greeting")),
                    () -> assertEquals(0, response.getBody().get("currentPage")),
                    () -> assertEquals(2L, response.getBody().get("totalItems")),
                    () -> assertEquals(1, response.getBody().get("totalPages")),
                    () -> assertNotNull(response.getBody().get("processingTimeMs"))
            );

            log.info("Exported data: {}", response.getBody());
        }


    private TranslationExportDTO createExportDto(String key, String content) {
            TranslationExportDTO dto = mock(TranslationExportDTO.class);
            when(dto.getKey()).thenReturn(key);
            when(dto.getContent()).thenReturn(content);
            return dto;
        }
    }

    @Nested
    @DisplayName("Get Translation by ID")
    class GetTranslationByIdTests {
        @Test
        @DisplayName("Should return translation when ID exists")
        void getById_withValidId_returnsTranslation() {
            log.info("Running test: getById_withValidId_returnsTranslation");
            Translation expected = createTranslation(VALID_ID, KEY_WELCOME, CONTENT_WELCOME,
                    createLocale(1L, LOCALE_EN), createTag(1L, "greeting"));

            when(service.getById(VALID_ID)).thenReturn(expected);

            ResponseEntity<Translation> response = controller.getById(VALID_ID);

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertNotNull(response.getBody())
            );
            log.info("Translation found: {}", response.getBody());
        }

        @Test
        @DisplayName("Should throw exception when ID doesn't exist")
        void getById_withInvalidId_throwsException() {
            log.info("Running test: getById_withInvalidId_throwsException");
            when(service.getById(INVALID_ID))
                    .thenThrow(new CustomException("Translation not found with ID: " + INVALID_ID));

            CustomException exception = assertThrows(CustomException.class, () -> {
                controller.getById(INVALID_ID);
            });

            log.warn("Expected exception thrown: {}", exception.getMessage());
            assertEquals("Translation not found with ID: " + INVALID_ID, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Update Translation")
    class UpdateTranslationTests {
        @Test
        @DisplayName("Should return updated translation when request is valid")
        void update_withValidRequest_returnsUpdatedTranslation() {
            log.info("Running test: update_withValidRequest_returnsUpdatedTranslation");
            TranslationRequestDTO request = createTranslationRequest("updated_key", 2L, "Updated content", 3L);
            Translation updated = createTranslation(VALID_ID, "updated_key", "Updated content",
                    createLocale(2L, LOCALE_FR), createTag(3L, "updated_tag"));

            when(service.update(eq(VALID_ID), any(TranslationRequestDTO.class))).thenReturn(updated);

            ResponseEntity<Translation> response = controller.update(VALID_ID, request);

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertNotNull(response.getBody())
            );
            log.info("Updated translation: {}", response.getBody());
        }

        @Test
        @DisplayName("Should throw exception when duplicate key exists")
        void update_withDuplicateKey_throwsException() {
            log.info("Running test: update_withDuplicateKey_throwsException");
            TranslationRequestDTO request = createTranslationRequest("duplicate_key", 2L, "Content", 3L);

            when(service.update(eq(VALID_ID), any(TranslationRequestDTO.class)))
                    .thenThrow(new CustomException("Duplicate translation key for the selected locale and tag"));

            CustomException exception = assertThrows(CustomException.class, () -> {
                controller.update(VALID_ID, request);
            });

            log.warn("Expected exception thrown: {}", exception.getMessage());
            assertEquals("Duplicate translation key for the selected locale and tag", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Translation")
    class DeleteTranslationTests {
        @Test
        @DisplayName("Should return success message when deletion succeeds")
        void delete_withValidId_returnsSuccessMessage() {
            log.info("Running test: delete_withValidId_returnsSuccessMessage");
            doNothing().when(service).delete(VALID_ID);

            ResponseEntity<String> response = controller.delete(VALID_ID);

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertEquals("Translation deleted successfully", response.getBody())
            );
            log.info("Delete successful for ID {}", VALID_ID);
        }

        @Test
        @DisplayName("Should throw exception when ID doesn't exist")
        void delete_withInvalidId_throwsException() {
            log.info("Running test: delete_withInvalidId_throwsException");
            doThrow(new CustomException("Translation not found with ID: " + INVALID_ID))
                    .when(service).delete(INVALID_ID);

            CustomException exception = assertThrows(CustomException.class, () -> {
                controller.delete(INVALID_ID);
            });

            log.warn("Expected exception thrown: {}", exception.getMessage());
            assertEquals("Translation not found with ID: " + INVALID_ID, exception.getMessage());
            verify(service).delete(INVALID_ID);
        }
    }
    @Nested
    @DisplayName("Export All Translations")
    class ExportAllTranslationsTests {
        @Test
        @DisplayName("Should return all translations with pagination info")
        void exportAll_returnsPaginatedTranslations() throws Exception {
            log.info("Running test: exportAll_returnsPaginatedTranslations");

            List<FullTranslationExportDTO> exportData = List.of(
                    new FullTranslationExportDTO(1L, "en", "greeting", "Hello", "general", LocalDateTime.now()),
                    new FullTranslationExportDTO(2L, "en", "farewell", "Goodbye", "general", LocalDateTime.now())
            );

            Page<FullTranslationExportDTO> mockPage = new PageImpl<>(
                    exportData,
                    PageRequest.of(0, 10000),
                    exportData.size()
            );

            when(service.exportAll(any(Pageable.class))).thenReturn(mockPage);

            CompletableFuture<ResponseEntity<Map<String, Object>>> futureResponse = controller.exportAll(0, 10000);
            ResponseEntity<Map<String, Object>> response = futureResponse.get(); // blocking call for test

            Map<String, Object> responseBody = response.getBody();
            List<FullTranslationExportDTO> translations = (List<FullTranslationExportDTO>) responseBody.get("translations");

            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertNotNull(responseBody),
                    () -> assertEquals(2, translations.size()),
                    () -> assertEquals("greeting", translations.get(0).getKey()),
                    () -> assertEquals("Hello", translations.get(0).getContent()),
                    () -> assertEquals("en", translations.get(0).getLocaleCode()),
                    () -> assertEquals("general", translations.get(0).getTagName()),
                    () -> assertEquals(0L, ((Number) responseBody.get("currentPage")).longValue()),
                    () -> assertEquals(2L, ((Number) responseBody.get("totalItems")).longValue()),
                    () -> assertEquals(1L, ((Number) responseBody.get("totalPages")).longValue()),
                    () -> assertNotNull(responseBody.get("processingTimeMs"))
            );

            log.info("Exported all translations: {}", responseBody);
        }
    }
        // Helper methods
    private TranslationRequestDTO createTranslationRequest(String key, Long localeId, String content, Long tagId) {
        TranslationRequestDTO request = new TranslationRequestDTO();
        request.setKey(key);
        request.setLocaleId(localeId);
        request.setContent(content);
        request.setTagId(tagId);
        return request;
    }

    private Translation createTranslation(Long id, String key, String content,
                                          com.service.translations.entity.Locale locale, com.service.translations.entity.Tag tag) {
        Translation translation = new Translation();
        translation.setId(id);
        translation.setKey(key);
        translation.setContent(content);
        translation.setLocale(locale);
        translation.setTag(tag);
        return translation;
    }

    private com.service.translations.entity.Locale createLocale(Long id, String code) {
        com.service.translations.entity.Locale locale = new com.service.translations.entity.Locale();
        locale.setId(id);
        locale.setCode(code);
        return locale;
    }

    private com.service.translations.entity.Tag createTag(Long id, String name) {
        com.service.translations.entity.Tag tag = new com.service.translations.entity.Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

//    private TranslationExportDto createExportDto(String key, String content) {
//        return new TranslationExportDto() {
//            @Override
//            public String getKey() { return key; }
//            @Override
//            public String getContent() { return content; }
//        };
//}
}
