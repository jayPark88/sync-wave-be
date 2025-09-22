package com.parker.common.util;

import com.parker.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

public class FileUtil {

    public static File multipartFileToFile(MultipartFile multipartFile) {
        // multipartFile의 원본 파일 이름을 사용하여 File 객체를 생성
        File file = new File(multipartFile.getOriginalFilename());
        try {
            // multipartFile의 데이터를 file로 전송
            multipartFile.transferTo(file);
            // 파일 전송이 완료되면 file 객체를 반환
            return file;
        } catch (IOException e) {
            // 파일 전송 중 예외 발생 시 FileTransferException 던짐
            throw new CustomException(FAIL_500.code(), "multipartFileToFile Fail!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
