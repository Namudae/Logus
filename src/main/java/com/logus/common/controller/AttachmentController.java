package com.logus.common.controller;

import com.logus.common.dto.AttachmentRequestDto;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final S3Service s3Service;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    /**
     * 이미지 임시저장 s3
     */
    @PostMapping("/temporary-image")
    public ApiResponse<AttachmentRequestDto> saveTempImage(@RequestParam("file") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        AttachmentType toAttachmentType = AttachmentType.TEMP;

        AttachmentRequestDto imageDto = null;
        String imgPath = "";
        try {
            imageDto = s3Service.tempUpload(image);
        }  catch (Exception e) {
            throw new CustomException(ErrorCode.AMAZON_SERVICE_ERROR);
        }

        return ApiResponse.ok(imageDto);
    }


}
