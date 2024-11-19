package com.ssafy.edu.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDTO {
    private String originalFileName;
    private String storedFileName;
    private String downloadUri;
    private String contentType;
    private long size;
}