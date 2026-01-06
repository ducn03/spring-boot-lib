package com.springboot.cdn.features.file;

import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.file.FileService;
import com.springboot.cdn.service.file.dto.CdnUploadRequest;
import com.springboot.lib.aop.LogActivity;
import com.springboot.lib.helper.ControllerHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Nơi tương tác với file
 */
@RestController
@RequestMapping("/app/cdn")
@Slf4j
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Upload file
     * POST /app/cdn/uploads
     */
    @LogActivity
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@ModelAttribute CdnUploadRequest request) throws Exception {
        CdnDTO result = fileService.uploadFile(request);
        return ControllerHelper.success(result);
    }

    /**
     * Get file
     * GET /app/cdn/{env}/{domain}/{group}/{name}
     */
    @LogActivity
    @GetMapping("/{env}/{domain}/{group}/{name}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String env,
            @PathVariable String domain,
            @PathVariable String group,
            @PathVariable String name,
            HttpServletRequest servletRequest
    ) throws Exception {
        String url = String.format("app/cdn/%s/%s/%s/%s", env, domain, group, name);
        log.info("Download request: {}", url);

        Resource resource = fileService.getFile(url);

        String contentType = servletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFilename + "\"")
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
                .body(resource);

    }
}
