package com.mahesha.media_service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@SpringBootApplication
public class MediaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaServiceApplication.class, args);
	}

	@Bean
	public Storage storage(@Value("${gcp.project-id}") String projectId, @Value("${gcp.credentials.location}") ClassPathResource credentialsResource) throws IOException {

		ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(credentialsResource.getInputStream());
		return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build().getService();
	}

}
