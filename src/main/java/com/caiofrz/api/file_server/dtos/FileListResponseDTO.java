package com.caiofrz.api.file_server.dtos;

import java.util.List;

public record FileListResponseDTO(
        String location,
        Integer size,
        List<String> files
) {
}
