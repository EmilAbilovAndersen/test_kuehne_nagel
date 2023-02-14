package com.andersen.test.cities;

import com.andersen.test.cities.dto.CityFilterDto;
import com.andersen.test.cities.dto.CityPageDto;
import com.andersen.test.cities.dto.CityRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityManagementService {

    private final CityRepository cityRepository;

    public CityPageDto getCities(CityFilterDto filterDto) {
        var pageRequest = PageRequest.of(filterDto.pageNumber(), filterDto.pageSize(), Sort.by("id"));
        Page<City> page;

        if (Objects.isNull(filterDto.name())) {
            log.info("Getting cities by page '{}' with page size '{}'", filterDto.pageNumber(), filterDto.pageSize());
            page = cityRepository.findAll(pageRequest);
        } else {
            log.info("Getting cities by name {}, page '{}' with page size '{}'", filterDto.name(), filterDto.pageNumber(), filterDto.pageSize());
            page = cityRepository.findAll(
                buildSpecificationByName(filterDto.name()),
                pageRequest
            );
        }

        return new CityPageDto(page.getContent(), page.getTotalElements(), page.getNumber(), page.getTotalPages());
    }

    public City update(CityRequestDto updated) {
        log.info("Updating city with id '{}'", updated.id());

        var exists = cityRepository.findById(updated.id())
            .orElseThrow(() -> new EntityNotFoundException("City not found"));

        exists.setName(updated.name());
        exists.setPhotoUrl(updated.photoUrl());

        return cityRepository.save(exists);
    }

    private Specification<City> buildSpecificationByName(String name) {
        return (root, query, builder)
            -> builder.like(builder.upper(root.get("name")), '%' + name.toUpperCase(Locale.ROOT) + '%');
    }
}
