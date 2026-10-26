package com.portfolio.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String dir) {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename()).toLowerCase();
        boolean ok = original.endsWith(".pdf") || original.endsWith(".doc") || original.endsWith(".docx");
        if (file.isEmpty() || !ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a PDF or Word resume");
        }
        try {
            Files.createDirectories(root);
            String name = UUID.randomUUID() + "-" + original.replaceAll("[^a-z0-9._-]", "_");
            Files.copy(file.getInputStream(), root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save file");
        }
    }

    public Resource load(String name) {
        Path p = root.resolve(name).normalize();
        if (!p.startsWith(root) || !Files.exists(p)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return new FileSystemResource(p);
    }
}
