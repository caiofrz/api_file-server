package com.caiofrz.api.file_server.services;

import com.caiofrz.api.file_server.config.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileConverterService {

  private final Path fileStoragePath;

  public FileConverterService(FileStorageProperties fileStorageProperties) {
    this.fileStoragePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
  }

  public String convertImageToPdf(InputStream inputStream, String fileName) {
    try {
      PDDocument pdf = new PDDocument();
      PDPage page = new PDPage(PDRectangle.A5);
      pdf.addPage(page);

      PDImageXObject image = PDImageXObject.createFromByteArray(pdf, inputStream.readAllBytes(), fileName);
      PDPageContentStream contentStream = new PDPageContentStream(pdf, page);

      //Redimensionado o tamanho da imagem de acordo o tamanho da folha
      float width = (float) (PDRectangle.A5.getWidth() * 0.5);
      float height = (float) (PDRectangle.A5.getHeight() * 0.75);
      //Alterando os valores dos eixos do inicio do desenho
      float locDrawInitX = 10;
      float locDrawInitY = 10;

      contentStream.drawImage(image, locDrawInitX, locDrawInitY, width, height);
      contentStream.close();

      fileName += ".pdf";
      pdf.save(fileStoragePath + "/" + fileName);
      log.info("PDF gerado com sucesso: " + fileName);
      pdf.close();

      return ServletUriComponentsBuilder.fromCurrentContextPath()
              .path("/api/files/download/").path(fileName)
              .toUriString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
