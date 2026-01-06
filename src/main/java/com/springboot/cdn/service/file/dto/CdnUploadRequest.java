package com.springboot.cdn.service.file.dto;

import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import com.springboot.lib.enums.EStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class CdnUploadRequest {
    private long id;

    @Enumerated(EnumType.STRING)
    private EDomain domain;

    @Enumerated(EnumType.STRING)
    private EEnv env;

    private String group;
    private String link;

    private int status = EStatus.ACTIVE.getValue();

    private Timestamp createdAt;
    private Timestamp updatedAt;

    private MultipartFile file;
}
