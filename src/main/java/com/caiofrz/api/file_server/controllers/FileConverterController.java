package com.caiofrz.api.file_server.controllers;

import com.caiofrz.api.file_server.dtos.FileUploadResponseDTO;
import com.caiofrz.api.file_server.exceptions.FileIOException;
import com.caiofrz.api.file_server.exceptions.UnsupportedFileTypeException;
import com.caiofrz.api.file_server.services.FileConverterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("api/files")
@Slf4j
@RequiredArgsConstructor
public class FileConverterController {

  private final FileConverterService converterService;

  @PostMapping("/convert-to-pdf")
  public ResponseEntity<FileUploadResponseDTO> convertToPDF(@RequestParam("file") MultipartFile file,
                                                            HttpServletRequest request) {
    String[] fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).split("\\.");
    String extension = fileName[1];
    String generatedPdfUri;
    try {
      generatedPdfUri = switch (extension) {
        case "jpg", "png", "jpeg" -> this.converterService.convertImageToPdf(file.getInputStream(), fileName[0]);
        default -> throw new UnsupportedFileTypeException("Formato de arquivo não suportado: " + extension);
      };
      return ResponseEntity.ok(new FileUploadResponseDTO(HttpStatus.OK, "Conversão bem sucessida!", generatedPdfUri));
    } catch (IOException e) {
      throw new FileIOException("Não foi possível converter o arquivo");
    }
  }
}
