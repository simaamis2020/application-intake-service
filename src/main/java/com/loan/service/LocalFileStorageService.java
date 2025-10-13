package com.loan.service;


import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper class for file storage
 */
@Service
public class LocalFileStorageService {

    private final Path baseDir;

    public LocalFileStorageService(@Value("${app.uploads.base-dir}") String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    /**
     * Saves a MultipartFile to disk and returns its file:// URI.
     * @param file the incoming file
     * @param subfolder optional logical subfolder (e.g., "paystubs")
     */
    public URI save(MultipartFile file, String subfolder, String fixedName) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("No file content to save.");

        Path targetDir = (subfolder == null || subfolder.isBlank()) ? baseDir : baseDir.resolve(safeSegment(subfolder));
        Files.createDirectories(targetDir);
       
        String ext = getExtension(file.getOriginalFilename()); // keep original extension
        String fileName;
        if (fixedName == null || fixedName.isBlank()) {
            fileName = sanitize(file.getOriginalFilename());
        } else {
            fileName = sanitize(fixedName);
            // keep extension if fixedName doesn’t have one
            if (!fileName.contains(".")) {
                fileName += getExtension(file.getOriginalFilename());
            }
        }

        Path target = targetDir.resolve(fileName).normalize();     if (!target.startsWith(baseDir)) throw new IOException("Invalid target path.");

        // Write to a temp file then replace 
        Path tmp = Files.createTempFile(targetDir, ".upload-", ".part");
        Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        return target.toUri();
    }
    // --- helpers ---

    private static String sanitize(String name) {
        // remove path separators and risky chars
        return name.replaceAll("[\\\\/\\p{Cntrl}]+", "_")
                   .replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }

    private static String getExtension(String name) {
        if (name == null) return "";
        int dot = name.lastIndexOf('.');
        return (dot >= 0) ? name.substring(dot) : "";
    }

    private static String safeSegment(String segment) {
        return sanitize(segment).toLowerCase(Locale.ROOT);
    }
    public void clearFileDirectory(String subfolder) throws IOException {
        Path targetDir = (subfolder == null || subfolder.isBlank())
                ? baseDir
                : baseDir.resolve(safeSegment(subfolder));

        if (!Files.exists(targetDir)) return;

        try (Stream<Path> files = Files.list(targetDir)) {
            files.filter(Files::isRegularFile).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    System.err.println("Warning: failed to delete " + path + ": " + e.getMessage());
                }
            });
        }
    }
}
