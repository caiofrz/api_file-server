package com.caiofrz.api.file_server.controllers;

import com.caiofrz.api.file_server.exceptions.ErrorResponse;
import com.caiofrz.api.file_server.exceptions.FileIOException;
import com.caiofrz.api.file_server.exceptions.UnsupportedFileTypeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.FileSystemNotFoundException;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(FileIOException.class)
  public ResponseEntity<ErrorResponse> handleIOException(FileIOException ex,
                                                         HttpServletRequest request) {
    log.warn(ex.getMessage());
    return response(LocalDateTime.now(), HttpStatus.BAD_REQUEST,
            ex.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request);
  }

  @ExceptionHandler(UnsupportedFileTypeException.class)
  public ResponseEntity<ErrorResponse> handleIOException(UnsupportedFileTypeException ex,
                                                         HttpServletRequest request) {
    log.warn(ex.getMessage());
    return response(LocalDateTime.now(), HttpStatus.BAD_REQUEST,
            ex.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request);
  }

  @ExceptionHandler(FileSystemNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileSystemNotFoundException(FileSystemNotFoundException ex,
                                                                         HttpServletRequest request) {
    log.warn(ex.getMessage());
    return response(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), request);
  }

  private ResponseEntity<ErrorResponse> response(final LocalDateTime timestamp,
                                                 final HttpStatus status,
                                                 final String error,
                                                 final String message,
                                                 final HttpServletRequest request) {
    return ResponseEntity.status(status)
            .body(new ErrorResponse(timestamp, status.value(), message, error, request.getRequestURI()));
  }
}
