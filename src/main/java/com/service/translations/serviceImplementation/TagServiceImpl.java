package com.service.translations.serviceImplementation;

import com.service.translations.dto.TagRequestDTO;
import com.service.translations.entity.Tag;
import com.service.translations.exception.CustomException;
import com.service.translations.repository.TagRepository;
import com.service.translations.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository repo;

    @Override
    public TagRequestDTO create(TagRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new CustomException("Tag name cannot be empty");
        }

        if (repo.existsByName(dto.getName())) {
            throw new CustomException("Tag with this name already exists");
        }

        Tag tag = Tag.builder().name(dto.getName().trim()).build();
        return mapToDto(repo.save(tag));
    }

    @Override
    public TagRequestDTO update(Long id, TagRequestDTO dto) {
        Tag tag = repo.findById(id).orElseThrow(() -> new CustomException("Tag not found"));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new CustomException("Tag name cannot be empty");
        }

        // Check for duplication excluding current
        if (repo.existsByName(dto.getName()) && !tag.getName().equals(dto.getName())) {
            throw new CustomException("Tag with this name already exists");
        }

        tag.setName(dto.getName().trim());
        return mapToDto(repo.save(tag));
    }


    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new CustomException("Tag not found");
        repo.deleteById(id);
    }

    @Override
    public TagRequestDTO get(Long id) {
        return mapToDto(repo.findById(id).orElseThrow(() -> new CustomException("Tag not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagRequestDTO> getAll() {
        return repo.findAllByOrderByIdDesc().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TagRequestDTO mapToDto(Tag tag) {
        return TagRequestDTO.builder().id(tag.getId()).name(tag.getName()).build();
    }
}