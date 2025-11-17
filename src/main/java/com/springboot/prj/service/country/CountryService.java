package com.springboot.prj.service.country;

import com.springboot.jpa.domain.Country;
import com.springboot.jpa.repository.CountryRepository;
import com.springboot.prj.service.country.dto.CountryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryDTO> getCountries() {
        return countryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CountryDTO toDTO(Country entity) {
        if (entity == null) {
            return null;
        }

        CountryDTO dto = new CountryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        dto.setNameVi(entity.getNameVi());
        dto.setPhoneCode(entity.getPhoneCode());
        dto.setCurrencyCode(entity.getCurrencyCode());

        return dto;
    }

    private Country toEntity(CountryDTO dto) {
        if (dto == null) {
            return null;
        }

        Country entity = new Country();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNameEn(dto.getNameEn());
        entity.setNameVi(dto.getNameVi());
        entity.setPhoneCode(dto.getPhoneCode());
        entity.setCurrencyCode(dto.getCurrencyCode());

        return entity;
    }
}
