package com.service.translations.serviceImplementation;

import com.service.translations.dto.LocaleRequestDTO;
import com.service.translations.entity.Locale;
import com.service.translations.exception.CustomException;
import com.service.translations.repository.LocaleRepository;
import com.service.translations.service.LocaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocaleServiceImpl implements LocaleService {
    private final LocaleRepository repo;

    @Override
    public LocaleRequestDTO create(LocaleRequestDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new CustomException("Locale code cannot be empty");
        }

        if (repo.existsByCode(dto.getCode())) {
            throw new CustomException("Locale with this code already exists");
        }

        Locale locale = Locale.builder().code(dto.getCode().trim()).build();
        return mapToDto(repo.save(locale));
    }

    @Override
    public LocaleRequestDTO update(Long id, LocaleRequestDTO dto) {
        Locale locale = repo.findById(id).orElseThrow(() -> new CustomException("Locale not found"));

        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new CustomException("Locale code cannot be empty");
        }

        // Check for duplication excluding current
        if (repo.existsByCode(dto.getCode()) && !locale.getCode().equals(dto.getCode())) {
            throw new CustomException("Locale with this code already exists");
        }

        locale.setCode(dto.getCode().trim());
        return mapToDto(repo.save(locale));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new CustomException("Locale not found");
        repo.deleteById(id);
    }

    @Override
    public LocaleRequestDTO get(Long id) {
        return mapToDto(repo.findById(id).orElseThrow(() -> new CustomException("Locale not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocaleRequestDTO> getAll() {
        return repo.findAllByOrderByIdDesc().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private LocaleRequestDTO mapToDto(Locale locale) {
        return LocaleRequestDTO.builder().id(locale.getId()).code(locale.getCode()).build();
    }
}
