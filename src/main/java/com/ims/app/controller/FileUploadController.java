package com.ims.app.controller;


import com.ims.app.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file")
public class FileUploadController {
    private final S3Service s3Service;


    @Autowired
    public FileUploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = s3Service.uploadFile(file);
        return ResponseEntity.ok(url);
    }

}
