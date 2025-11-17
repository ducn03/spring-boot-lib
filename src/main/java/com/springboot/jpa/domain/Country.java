package com.springboot.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "M_COUNTRY")
@ToString
public class Country extends BaseEntity {
    /**
     * Mã quốc gia, ví dụ: "VN", "US"
     */
    @Column(name = "code", nullable = false, length = 10, unique = true)
    private String code;

    /**
     * Tên quốc gia tiếng Anh, ví dụ: "Vietnam"
     */
    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    /**
     * Tên quốc gia tiếng Việt, ví dụ: "Việt Nam"
     */
    @Column(name = "name_vi", length = 100)
    private String nameVi;

    /**
     * Mã điện thoại quốc gia, ví dụ: "+84"
     */
    @Column(name = "phone_code", length = 10)
    private String phoneCode;

    /**
     * Mã tiền tệ, ví dụ: "VND", "USD"
     */
    @Column(name = "currency_code", length = 10)
    private String currencyCode;
}
