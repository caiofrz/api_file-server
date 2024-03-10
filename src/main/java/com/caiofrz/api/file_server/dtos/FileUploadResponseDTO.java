package com.caiofrz.api.file_server.dtos;

import org.springframework.http.HttpStatus;

public record FileUploadResponseDTO(HttpStatus status,
                                    String message,
                                    String fileDownloadUri
) {
}
