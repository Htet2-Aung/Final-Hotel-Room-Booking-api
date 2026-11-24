package com.hostmdy.hotelbooking.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

	public String store(MultipartFile file) {
		try {
			Files.createDirectories(uploadDir);

			String original = StringUtils
					.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
			String extension = "";
			int dotIndex = original.lastIndexOf('.');
			if (dotIndex >= 0) {
				extension = original.substring(dotIndex);
			}

			String filename = UUID.randomUUID().toString() + extension;
			Path target = uploadDir.resolve(filename);
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

			return filename;
		} catch (IOException e) {
			throw new RuntimeException("Could not store file: " + e.getMessage(), e);
		}
	}
}
