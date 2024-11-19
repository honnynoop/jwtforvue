package com.ssafy.edu.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.edu.dto.request.FileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class   ImageService{
	
	@Value("${file.upload.directory}")
    private String UPLOAD_DIR;

	public List<FileDTO> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::storeFile)
                .collect(Collectors.toList());
    }
/*
    public FileDTO storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Invalid file path sequence " + originalFileName);
            }

            Path targetLocation = Paths.get(UPLOAD_DIR)
                .resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return FileDTO.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .downloadUri("/image/" + storedFileName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName, ex);
        }
    }
*/
    public UrlResource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR)
                .resolve(fileName).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }
    
    /**
     * 파일명에서 확장자를 추출합니다.
     * @param fileName 파일명
     * @return 확장자 (점(.)을 포함, 확장자가 없는 경우 빈 문자열 반환)
     */
    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // 마지막 점(.)의 위치를 찾습니다
        int lastDotIndex = fileName.lastIndexOf(".");
        
        // 확장자가 없거나 숨김 파일인 경우 (예: .gitignore)
        if (lastDotIndex < 1) {
            return "";
        }

        // 파일명이 점으로 끝나는 경우
        if (lastDotIndex == fileName.length() - 1) {
            return "";
        }

        // 추출된 확장자를 소문자로 변환
        String extension = fileName.substring(lastDotIndex).toLowerCase();

        // 허용된 이미지 확장자인지 검증 (선택적)
        if (isValidImageExtension(extension)) {
            return extension;
        }

        return "";
    }

    /**
     * 허용된 이미지 확장자인지 검증합니다.
     * @param extension 확장자 (점(.)을 포함)
     * @return 허용된 확장자인 경우 true
     */
    private boolean isValidImageExtension(String extension) {
        // 허용할 이미지 확장자 목록
        Set<String> allowedExtensions = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
        );
        
        return allowedExtensions.contains(extension);
    }

    /**
     * 파일의 실제 Content Type을 확인합니다.
     * @param file MultipartFile
     * @return 파일이 이미지인 경우 true
     */
    private boolean isImageFile(MultipartFile file) {
        try {
            // 파일의 실제 content type 확인
            String contentType = file.getContentType();
            if (contentType == null) {
                return false;
            }
            return contentType.startsWith("image/");
        } catch (Exception e) {
            log.error("File type check failed", e);
            return false;
        }
    }

    // storeFile 메서드에서 파일 유효성 검증 예시
    public FileDTO storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 파일이 이미지인지 확인
        if (!isImageFile(file)) {
            throw new RuntimeException("Only image files are allowed");
        }

        String extension = extractExtension(originalFileName);
        if (extension.isEmpty()) {
            throw new RuntimeException("Invalid file extension");
        }

        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Invalid file path sequence " + originalFileName);
            }

            Path targetLocation = Paths.get(UPLOAD_DIR)
                .resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return FileDTO.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .downloadUri("/image/" + storedFileName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName, ex);
        }
    }
}