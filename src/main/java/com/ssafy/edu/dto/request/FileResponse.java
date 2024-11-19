package com.ssafy.edu.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
    private String fileName;
    private String fileDownloadUri;
    private long size;
}