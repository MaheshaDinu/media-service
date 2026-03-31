package com.mahesha.media_service;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@SpringBootApplication
public class MediaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaServiceApplication.class, args);
	}

	@Bean
	public Storage storage() throws IOException {

		// ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(credentialsResource.getInputStream());
		// return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build().getService();
		return StorageOptions.getDefaultInstance().getService();
	}

}
