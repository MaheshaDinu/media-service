package com.mahesha.media_service.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.mahesha.media_service.service.MediaService;

@Service
public class MediaServiceImpl implements MediaService {

    private final Storage storage;

    public MediaServiceImpl(Storage storage) {
        this.storage = storage;
    }

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Override
    public String uploadCoverImage(MultipartFile file) {
        return processAndUpload(file, "covers/", "image/");
    }

    @Override
    public String uploadBookPdf(MultipartFile file) {
        return processAndUpload(file, "pdfs/", "application/pdf");
    }

    private String processAndUpload(MultipartFile file, String folderPrefix, String expectedContentType) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is emplty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith(expectedContentType)) {
            throw new IllegalArgumentException("Invalid file type. Expected " + expectedContentType);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String savedFileName = folderPrefix + UUID.randomUUID() + extension;

        try (var inputStream = file.getInputStream()) {
            BlobId blobId = BlobId.of(bucketName, savedFileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
            storage.createFrom(blobInfo, inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process and upload file", e);
        }

        return savedFileName;
    }

    @Override
    public List<String> listAll(String folder) {
        // If folder is "covers", we search for "covers/"
        String prefix = (folder != null && !folder.isEmpty()) ? folder + "/" : "";

        // Use BlobListOption to tell GCS to filter by prefix
        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(prefix));

        List<String> filenames = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            // We exclude the folder name itself if it appears as a result
            if (!blob.getName().equals(prefix)) {
                filenames.add(blob.getName());
            }
        }
        return filenames;
    }

    @Override
    public Resource load(String filename) {

        byte[] content = storage.readAllBytes(bucketName, filename);
        if (content == null) {
            throw new IllegalArgumentException("File not found in cloud storage: " + filename);
        }
        return new ByteArrayResource(content);

    }

    @Override
    public void delete(String filename) {

        Blob blob = storage.get(bucketName, filename);
        if (blob == null) {
            throw new IllegalArgumentException("File not found in cloud storage: " + filename);
        }

        storage.delete(blob.getBlobId());

    }
}
