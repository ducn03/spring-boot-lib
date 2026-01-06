package com.springboot.cdn.service.file;

import com.springboot.cdn.exception.AppErrorCodes;
import com.springboot.cdn.service.cdn.CdnMapper;
import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.file.dto.CdnUploadRequest;
import com.springboot.jpa.domain.Cdn;
import com.springboot.jpa.repository.cdn.CdnRepository;
import com.springboot.lib.exception.AppException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${cdn.base-path:tgh/cdn}")
    private String cdnBasePath;

    private final Set<String> createdDirs = ConcurrentHashMap.newKeySet();

    private final CdnRepository cdnRepository;
    private final CdnMapper cdnMapper;

    public FileServiceImpl(CdnRepository cdnRepository, CdnMapper cdnMapper) {
        this.cdnRepository = cdnRepository;
        this.cdnMapper = cdnMapper;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(cdnBasePath).toAbsolutePath().normalize());
            log.info("Initialized base CDN directory at {}", cdnBasePath);
        } catch (Exception e) {
            throw new AppException(AppErrorCodes.FILE.FILE_INIT_ERROR, "Cannot initialize base CDN directory");
        }
    }

    @Override
    public CdnDTO uploadFile(CdnUploadRequest request) {
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new AppException(AppErrorCodes.FILE.FILE_EMPTY, "File is empty or missing");
        }

        try {
            Path dir = ensureDirectory(request.getEnv().name(), request.getDomain().name(), request.getGroup());

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getExtension(originalFilename);
            String hashedName = hashFileName(originalFilename) + (extension.isEmpty() ? "" : "." + extension);

            Path targetPath = dir.resolve(hashedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Đường dẫn relative để lưu DB (ví dụ: tgh/cdn/DEV/DOMAIN/group/abc123.png)
            String relativePath = String.join("/",
                    cdnBasePath, request.getEnv().name(),
                    request.getDomain().name(), request.getGroup(), hashedName
            );

            // Tạo entity
            Cdn cdn = new Cdn();
            cdn.setEnv(request.getEnv());
            cdn.setDomain(request.getDomain());
            cdn.setGroup(request.getGroup().trim());
            cdn.setLink("/" + relativePath);
            cdn.setStatus(request.getStatus());

            cdnRepository.save(cdn);

            log.info("Uploaded file: originalName={} savedAs={} size={}B path={}",
                    originalFilename, hashedName, file.getSize(), targetPath);

            return cdnMapper.toDTO(cdn);
        } catch (Exception e) {
            throw new AppException(AppErrorCodes.FILE.FILE_UPLOAD_ERROR, "Cannot upload file");
        }
    }

    @Override
    public Resource getFile(String url) {
        try {
            String relativePath = url.startsWith("/") ? url.substring(1) : url;
            Path filePath = Paths.get(relativePath).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new AppException(AppErrorCodes.FILE.FILE_NOT_FOUND, "File not found: " + url);
            }

            log.info("Served file: {}", filePath);
            return resource;
        } catch (MalformedURLException e) {
            throw new AppException(AppErrorCodes.FILE.FILE_INVALID_URL, "Invalid file URL: " + url);
        }
    }

    // ============== Helpers ==============

    private Path ensureDirectory(String env, String domain, String group) throws Exception {
        Path dir = Paths.get(cdnBasePath, env, domain, group).toAbsolutePath().normalize();
        if (!createdDirs.contains(dir.toString())) {
            Files.createDirectories(dir);
            createdDirs.add(dir.toString());
        }
        return dir;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < filename.length() - 1)
                ? filename.substring(dotIndex + 1)
                : "";
    }

    private String hashFileName(String filename) throws NoSuchAlgorithmException {
        String uuid = UUID.randomUUID().toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest((filename + uuid).getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.substring(0, 32);
    }
}
