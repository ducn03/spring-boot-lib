package com.springboot.cdn.service.file;

import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.file.dto.CdnUploadRequest;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;

public interface FileService {
    CdnDTO uploadFile(CdnUploadRequest request) throws Exception;
    Resource getFile(String url) throws MalformedURLException;
}
