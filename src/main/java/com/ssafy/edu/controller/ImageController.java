package com.ssafy.edu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ssafy.edu.dto.request.FileDTO;

import com.ssafy.edu.service.ImageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<List<FileDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<FileDTO> uploadedFiles = imageService.uploadFiles(files);
        return ResponseEntity.ok(uploadedFiles);
    }

    @PostMapping("/upload/single")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        FileDTO uploadedFile = imageService.storeFile(file);
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<UrlResource> downloadFile(@PathVariable String fileName, 
                                              HttpServletRequest request) {
    	UrlResource resource = imageService.loadFileAsResource(fileName);
        String contentType = null;
        
        try {
            contentType = request.getServletContext()
                .getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}