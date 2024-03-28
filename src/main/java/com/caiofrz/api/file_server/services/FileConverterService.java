package com.caiofrz.api.file_server.services;

import com.caiofrz.api.file_server.config.FileStorageProperties;
import com.caiofrz.api.file_server.exceptions.FileIOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
    } catch (IOException ex) {
      log.warn("Conversão falhou: " + ex.getMessage());
      throw new FileIOException("Não foi possivel converter esse arquivo para pdf!");
    }
  }

  public String convertDocxToPdf(InputStream inputStream, String fileName) throws IOException {
    String text = this.extractTextFromDocx(inputStream);
    try (XWPFDocument document = new XWPFDocument(inputStream);
         PDDocument pdf = new PDDocument()) {

      PDPage page = new PDPage();
      pdf.addPage(page);

      try (PDPageContentStream contentStream = new PDPageContentStream(pdf, page)) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
          XWPFRun run = paragraph.getRuns().get(0); // Assume que cada parágrafo possui apenas um run
          contentStream.beginText();
          contentStream.newLineAtOffset(100, 700); // Posição inicial do texto
          contentStream.setFont(PDType0Font.load(pdf, inputStream), 12);
          contentStream.showText(run.getText(0));
          contentStream.newLine();
          contentStream.endText();
        }
      }

      fileName += ".pdf";
      pdf.save(fileStoragePath + "/" + fileName);
      log.info("PDF gerado com sucesso: " + fileName);
      pdf.close();

      return ServletUriComponentsBuilder.fromCurrentContextPath()
              .path("/api/files/download/").path(fileName)
              .toUriString();
    } catch (IOException ex) {
      throw new FileIOException("Não foi possivel converter esse arquivo para pdf!");
    }
  }

  private String extractTextFromDocx(InputStream inputStream) throws IOException {
    try (XWPFDocument document = new XWPFDocument(inputStream);
         XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
      return extractor.getText();
    }
  }
}

