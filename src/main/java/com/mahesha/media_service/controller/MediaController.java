package com.mahesha.media_service.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahesha.media_service.service.MediaService;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload/cover")
    public ResponseEntity<String> uploadCover(@RequestParam("file") MultipartFile file) {
        String savedFilename = mediaService.uploadCoverImage(file);
        return new ResponseEntity<>(savedFilename, HttpStatus.CREATED);
    }

    @PostMapping("/upload/pdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        String savedFilename = mediaService.uploadBookPdf(file);
        return new ResponseEntity<>(savedFilename, HttpStatus.CREATED);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile(@RequestParam("filename") String filename) {
        Resource file = mediaService.load(filename);

        // Determine content type dynamically (image/jpeg or application/pdf)
        String contentType = filename.startsWith("covers/") ? "image/jpeg" : "application/pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(file);
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> listAllFiles(@RequestParam(required = false) String type) {
        // type could be "covers" or "pdfs"
        List<String> files = mediaService.listAll(type);
        return ResponseEntity.ok(files);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename) {
        mediaService.delete(filename);
        return ResponseEntity.ok("File deleted successfully: " + filename);
    }

}
