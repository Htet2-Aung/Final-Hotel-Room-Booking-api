package com.hostmdy.hotelbooking.resource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hostmdy.hotelbooking.service.FileStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000" })
public class FileUploadController {

	public static final String UPLOAD_BASE_URL = "http://localhost:8181/uploads/";

	private final FileStorageService fileStorageService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return new ResponseEntity<String>("Please select a file to upload", HttpStatus.BAD_REQUEST);
		}

		String filename = fileStorageService.store(file);

		Map<String, String> response = new HashMap<>();
		response.put("filename", filename);
		response.put("url", UPLOAD_BASE_URL + filename);

		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}
}
