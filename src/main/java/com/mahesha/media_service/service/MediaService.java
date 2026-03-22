package com.mahesha.media_service.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    
    String uploadCoverImage(MultipartFile file);
    String uploadBookPdf(MultipartFile file);
    List<String> listAll(String folder);
    Resource load( String fileName);
    void delete(String filename);
}
