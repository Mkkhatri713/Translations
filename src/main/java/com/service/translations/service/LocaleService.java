package com.service.translations.service;

import com.service.translations.dto.LocaleRequestDTO;

import java.util.List;

public interface LocaleService {
    LocaleRequestDTO create(LocaleRequestDTO dto);
    LocaleRequestDTO update(Long id, LocaleRequestDTO dto);
    void delete(Long id);
    LocaleRequestDTO get(Long id);
    List<LocaleRequestDTO> getAll();
}