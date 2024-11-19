package com.ssafy.edu.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileUploadUtil {
	
	@Value("${file.upload.directory}")
	private   String UPLOAD_DIR ;

/*
    public FileUploadUtil() {
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
*/
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> fileNames = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + extension;
                
                // 수정
                Path targetLocation = Paths.get(UPLOAD_DIR).resolve(newFileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                //File dest = new File(UPLOAD_DIR + "/"+newFileName);
                //file.transferTo(dest);
                fileNames.add(newFileName);
            }
        }
        
        return fileNames;
    }

    public void deleteFile(String fileName) {
        File file = new File(UPLOAD_DIR +  "/"+fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
