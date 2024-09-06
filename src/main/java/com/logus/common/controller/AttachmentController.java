package com.logus.common.controller;

import com.logus.common.dto.AttachmentDisplayDto;
import com.logus.common.dto.AttachmentRequestDto;
import com.logus.common.entity.AttachmentType;
import com.logus.common.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @GetMapping("/tempImage")
    public String newItem() {
        logger.info("GET request to /tempImage received.");
        return "image-form";
    }

    /**
     * 이미지 임시저장
     */
    @PostMapping("/tempImage")
    public ResponseEntity<AttachmentRequestDto> saveTempImage(@RequestParam("file") MultipartFile image
//                                                              , @RequestParam("attachmentType")String attachmentType
    ) {
        if (image == null || image.isEmpty()) {
            logger.warn("File is empty or null.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        AttachmentType toAttachmentType = AttachmentType.TEMP;

        AttachmentRequestDto imageDto = null;
        try {
            logger.info("Storing file with attachment type: {}", toAttachmentType);
            imageDto = attachmentService.storeFile(image, toAttachmentType);
            logger.info("File stored successfully with filename: {}", imageDto.getFilename());
        }  catch (Exception e) {
            logger.error("Error storing file", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(imageDto, HttpStatus.OK);
    }

}
