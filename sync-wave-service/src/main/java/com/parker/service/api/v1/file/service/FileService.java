package com.parker.service.api.v1.file.service;


import com.parker.service.api.v1.file.dto.FileDto;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.FileEntity;
import com.parker.common.jpa.repository.FileRepository;
import com.parker.common.resonse.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-path}")
    private String fileUploadPath;
    private final FileRepository fileRepository;
    private final MessageSource messageSource;

    public CommonResponse<List<FileEntity>> fileUpload(FileDto fileDto) {

        List<FileEntity> fileEntityList = new ArrayList<>();

        for (MultipartFile multipartFile : fileDto.getMultipartFiles()) {
            String fileName = multipartFile.getOriginalFilename();
            Path directoryPath = Paths.get(fileUploadPath);

            if (!StringUtils.hasLength(fileName)) {
                throw new CustomException(FAIL_500.code(), messageSource.getMessage("file.name.not.null", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Path filePath = directoryPath.resolve(fileName);

            // 디렉토리가 존재하지 않으면 생성
            try {
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }
            } catch (IOException e) {
                throw new CustomException(FAIL_500.code(), messageSource.getMessage("file.folder.create.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 파일 저장
            try {
                Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new CustomException(FAIL_500.code(), messageSource.getMessage("file.upload.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            FileEntity fileEntity =
                    FileEntity.builder()
                            .name(fileName)
                            .filePath(filePath.toString())
                            .fileSize(multipartFile.getSize())
                            .build();

            fileRepository.save(fileEntity);
            fileEntityList.add(fileEntity);
        }
        return new CommonResponse<>(fileEntityList);
    }

    public String deleteLegacyFile(Long fileSeq) {
        Optional<FileEntity> fileEntity = fileRepository.findById(fileSeq);

        if (fileEntity.isEmpty())
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("file.delete.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        File file = new File(fileEntity.get().getFilePath());

        if (file.delete()) {// db삭제
            fileRepository.deleteById(fileSeq);
            return fileEntity.get().getName();
        } else {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("file.delete.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
