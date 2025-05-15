package com.service.translations.tesController;

import com.service.translations.controller.LocaleController;
import com.service.translations.dto.LocaleRequestDTO;
import com.service.translations.service.LocaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LocaleControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(LocaleControllerTest.class);

    @Mock
    private LocaleService localeService;

    @InjectMocks
    private LocaleController localeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(localeController).build();
    }

    @Test
    public void testCreateLocale() throws Exception {
        // Adjusting to reflect the "code" value as per your actual API response
        LocaleRequestDTO localeRequestDTO = new LocaleRequestDTO(1L, "en");
        when(localeService.create(any(LocaleRequestDTO.class))).thenReturn(localeRequestDTO);

        logger.info("Running testCreateLocale...");

        mockMvc.perform(post("/api/locales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"en\"}"))  // No "name" field, only "code"
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("en"));  // Check for "code" value

        logger.info("Test createLocale passed.");
    }

    @Test
    public void testUpdateLocale() throws Exception {
        // Adjusting to reflect the "code" value as per your actual API response
        LocaleRequestDTO localeRequestDTO = new LocaleRequestDTO(1L, "en");
        when(localeService.update(any(Long.class), any(LocaleRequestDTO.class))).thenReturn(localeRequestDTO);

        logger.info("Running testUpdateLocale...");

        mockMvc.perform(put("/api/locales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"en\"}"))  // No "name" field, only "code"
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("en"));  // Check for "code" value

        logger.info("Test updateLocale passed.");
    }

    @Test
    public void testDeleteLocale() throws Exception {
        logger.info("Running testDeleteLocale...");

        mockMvc.perform(delete("/api/locales/1"))
                .andExpect(status().isNoContent());

        logger.info("Test deleteLocale passed.");
    }

    @Test
    public void testGetLocale() throws Exception {
        // Adjusting to reflect the "code" value as per your actual API response
        LocaleRequestDTO localeRequestDTO = new LocaleRequestDTO(1L, "en");
        when(localeService.get(1L)).thenReturn(localeRequestDTO);

        logger.info("Running testGetLocale...");

        mockMvc.perform(get("/api/locales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("en"));  // Check for "code" value

        logger.info("Test getLocale passed.");
    }

    @Test
    public void testGetAllLocales() throws Exception {
        // Adjusting to reflect the "code" value as per your actual API response
        LocaleRequestDTO localeRequestDTO = new LocaleRequestDTO(1L, "en");
        when(localeService.getAll()).thenReturn(List.of(localeRequestDTO));

        logger.info("Running testGetAllLocales...");

        mockMvc.perform(get("/api/locales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("en"));  // Check for "code" value

        logger.info("Test getAllLocales passed.");
    }
}
