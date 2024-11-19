package com.ssafy.edu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFile {
    private Long id;
    private Long boardId;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String contentType;
    private String createdAt;
    
    // 파일 URL 생성을 위한 메서드
    /*
    public String getFileUrl() {
        return "/uploads/" + this.storedFilename;
    }
    */
    
    // 파일 확장자 추출
    public String getFileExtension() {
        if (originalFilename == null) return "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        return lastDotIndex > -1 ? originalFilename.substring(lastDotIndex + 1) : "";
    }
    
    // 파일 크기를 읽기 쉬운 형태로 변환
    public String getFormattedSize() {
        if (fileSize == null) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));
        return String.format("%.1f %s", 
            fileSize / Math.pow(1024, digitGroups), 
            units[digitGroups]);
    }
    
    // 이미지 파일 여부 확인
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }
}