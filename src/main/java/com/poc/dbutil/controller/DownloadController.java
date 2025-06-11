package com.poc.dbutil.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DownloadController {

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(
        @RequestParam("fileName") String fileName
    ) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Unable to Continue, Missing or Empty fileName param");
        }

        try {
            final var resource = new ClassPathResource("sql/" + fileName); // resources/sql"

            if (!resource.exists()) {
                throw new RuntimeException("File NOT found: " + fileName);
            }

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing request. " + ex.getMessage());
        }
    }

}

