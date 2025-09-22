package com.parker.service.api.v1.file.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FileDto {
    private Long fileSeq;
    List<MultipartFile> multipartFiles;
}
