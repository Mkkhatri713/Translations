package com.service.translations.tesController;

import com.service.translations.controller.TagController;
import com.service.translations.dto.TagRequestDTO;
import com.service.translations.service.TagService;
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

public class TagControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TagControllerTest.class);

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @Test
    public void testCreateTag() throws Exception {
        // Create a TagRequest object to mock the response, including the id field
        TagRequestDTO tagRequestDTO = new TagRequestDTO(1L, "mobile");

        // Mock the service call to return the TagRequest object
        when(tagService.create(any(TagRequestDTO.class))).thenReturn(tagRequestDTO);

        logger.info("Running testCreateTag...");

        // Perform the POST request and verify the response
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mobile\"}")) // Name field in request body
                .andExpect(status().isCreated()) // Expecting HTTP 201 Created status
                .andExpect(jsonPath("$.id").value(1)) // Verifying the "id" field in the response
                .andExpect(jsonPath("$.name").value("mobile")); // Verifying the "name" field in the response

        logger.info("Test createTag passed.");
    }

    @Test
    public void testUpdateTag() throws Exception {
        TagRequestDTO tagRequestDTO = new TagRequestDTO(1L, "Updated Tag One");
        when(tagService.update(any(Long.class), any(TagRequestDTO.class))).thenReturn(tagRequestDTO);

        logger.info("Running testUpdateTag...");

        mockMvc.perform(put("/api/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Tag One\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tag One"));

        logger.info("Test updateTag passed.");
    }

    @Test
    public void testDeleteTag() throws Exception {
        logger.info("Running testDeleteTag...");

        mockMvc.perform(delete("/api/tags/1"))
                .andExpect(status().isNoContent());

        logger.info("Test deleteTag passed.");
    }

    @Test
    public void testGetTag() throws Exception {
        TagRequestDTO tagRequestDTO = new TagRequestDTO(1L, "Tag One");
        when(tagService.get(1L)).thenReturn(tagRequestDTO);

        logger.info("Running testGetTag...");

        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tag One"));

        logger.info("Test getTag passed.");
    }

    @Test
    public void testGetAllTags() throws Exception {
        TagRequestDTO tagRequestDTO = new TagRequestDTO(1L, "Tag One");
        when(tagService.getAll()).thenReturn(List.of(tagRequestDTO));

        logger.info("Running testGetAllTags...");

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tag One"));

        logger.info("Test getAllTags passed.");
    }
}
