package com.springboot.jpa.domain;

import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "`U_CDN`")
public class Cdn extends BaseEntity{
    /**
     * Lĩnh vực GOLF_HOUSE, LEGENDS_TOUR
     */
    @Column(name = "domain")
    @Enumerated(EnumType.STRING)
    private EDomain domain;

    /**
     * Cấu hình môi trường DEV, PROD
     */
    @Column(name = "env")
    @Enumerated(EnumType.STRING)
    private EEnv env;

    @Column(name = "`group`")
    private String group;

    @Column(name = "link")
    private String link;
}
