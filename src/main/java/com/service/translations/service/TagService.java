package com.service.translations.service;

import com.service.translations.dto.TagRequestDTO;

import java.util.List;

public interface TagService {
    TagRequestDTO create(TagRequestDTO dto);
    TagRequestDTO update(Long id, TagRequestDTO dto);
    void delete(Long id);
    TagRequestDTO get(Long id);
    List<TagRequestDTO> getAll();
}