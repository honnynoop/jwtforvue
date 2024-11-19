package com.ssafy.edu.controller;

import com.ssafy.edu.model.BoardFile;
import com.ssafy.edu.security.JwtTokenProvider;
import com.ssafy.edu.service.BoardFileService;
import com.ssafy.edu.service.BoardService;
import com.ssafy.edu.service.UserService;
import com.ssafy.edu.util.FileUploadUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Tag(name = "File", description = "파일 다운로드 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
	
	private final  BoardFileService boardFileService;
	
	@Value("${file.upload.directory}")
	private   String UPLOAD_DIR ;
	
	//http://localhost:8080/api/files/download/1
    @Operation(
        summary = "파일 다운로드",
        description = "게시판 첨부파일을 다운로드합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
        }
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "파일 ID") @PathVariable Long fileId) {
        try {
            BoardFile boardFile = boardFileService.getBoardFile(fileId);
            Path filePath = Paths.get(UPLOAD_DIR + "/"+boardFile.getStoredFilename());
            Resource resource = new UrlResource(filePath.toUri());

            String encodedFilename = URLEncoder.encode(boardFile.getOriginalFilename(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(boardFile.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}