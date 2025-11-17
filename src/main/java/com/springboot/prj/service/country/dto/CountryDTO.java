package com.springboot.prj.service.country.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CountryDTO {
    private Long id;
    private String code;
    private String nameEn;
    private String nameVi;
    private String phoneCode;
    private String currencyCode;
}
