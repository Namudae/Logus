package com.logus.common.controller;

import com.logus.common.dto.AttachmentRequestDto;
import com.logus.common.entity.AttachmentType;
import com.logus.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class AttachmentController {

    private final S3Service s3Service;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @GetMapping("/temporary-image")
    public String newItem() {
        return "image-form";
    }

    /**
     * 이미지 임시저장 s3
     */
    @PostMapping("/temporary-image")
    public ResponseEntity<AttachmentRequestDto> saveTempImage(@RequestParam("file") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        AttachmentType toAttachmentType = AttachmentType.TEMP;

        AttachmentRequestDto imageDto = null;
        String imgPath = "";
        try {
            imageDto = s3Service.tempUpload(image);
        }  catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(imageDto, HttpStatus.OK);
    }

    /**
     * 이미지 임시저장 v1
     */
//    @PostMapping("/tempImage")
//    public ResponseEntity<AttachmentRequestDto> saveTempImage(@RequestParam("file") MultipartFile image) {
//        if (image == null || image.isEmpty()) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//
//        AttachmentType toAttachmentType = AttachmentType.TEMP;
//
//        AttachmentRequestDto imageDto = null;
//        try {
////            imageDto = attachmentService.storeFile(image, toAttachmentType);
//            String imgPath = s3Service.upload(image);
//
//            logger.info("File stored successfully with filename: {}", imageDto.getFilename());
//        }  catch (Exception e) {
////            logger.error("Error storing file", e);
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>(imageDto, HttpStatus.OK);
//    }

}
