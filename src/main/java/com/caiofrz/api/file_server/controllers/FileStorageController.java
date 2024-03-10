package com.caiofrz.api.file_server.controllers;

import com.caiofrz.api.file_server.config.FileStorageProperties;
import com.caiofrz.api.file_server.dtos.FileUploadResponseDTO;
import com.caiofrz.api.file_server.exceptions.FileIOException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("api/files")
@Slf4j
public class FileStorageController {

  private final Path fileStoragePath;

  public FileStorageController(FileStorageProperties fileStorageProperties) {
    this.fileStoragePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
  }

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponseDTO> upload(@RequestParam("file") MultipartFile file) {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    try {
      Path targetPath = fileStoragePath.resolve(fileName);
      file.transferTo(targetPath);

      String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/files/download/").path(fileName).toUriString();

      log.info("Upload bem sucessido: " + fileName);
      return ResponseEntity.ok(new FileUploadResponseDTO(HttpStatus.OK, "Upload bem sucessido!", fileDownloadUri));
    } catch (IOException ex) {
      throw new FileIOException("Não foi possível fazer o upload do arquivo " + fileName);
    }
  }

  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<Resource> download(@PathVariable String fileName, HttpServletRequest request) {
    Path filePath = fileStoragePath.resolve(fileName).normalize();

    try {
      Resource resource = new UrlResource(filePath.toUri());
      String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

      if (contentType == null) contentType = "application/octet-stream"; // ContentType genérico para arquivos

      log.info("Download bem sucessido: " + fileName);
      return ResponseEntity.ok().
              contentType(MediaType.parseMediaType(contentType)).
              header(HttpHeaders.CONTENT_DISPOSITION,
                      "attachment: filename=\"" + resource.getFilename() + "\"")
              .body(resource);
    } catch (IOException ex) {
      if (ex instanceof FileNotFoundException)
        throw new FileIOException("Ocorreu um erro na recuperação do arquivo para download: ARQUIVO NÃO ENCONTRADO!");

      if (ex instanceof MalformedURLException)
        throw new FileIOException("Ocorreu um erro na recuperação do arquivo para download: ERRO NA FORMAÇÃO DA URL!");

      throw new FileIOException("Ocorreu um erro na recuperação do arquivo para download!");
    }
  }

  @GetMapping("/list")
  public ResponseEntity<List<String>> listFiles() {
    try {
      List<String> files = Files.list(fileStoragePath).
              map(Path::getFileName).
              map(Path::toString).
              toList();

      return ResponseEntity.ok(files);
    } catch (IOException ex) {
      throw new FileSystemNotFoundException("Não foi possível recuperar os arquivos!");
    }
  }
}
