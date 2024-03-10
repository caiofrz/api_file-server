package com.caiofrz.api.file_server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
public class FileStorageProperties {

  @Value("${file.upload-dir}")
  private String uploadDir;
}
