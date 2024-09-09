package com.logus.common.service;

import com.logus.common.dto.AttachmentRequestDto;
import com.logus.common.entity.AttachmentType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    /**
     * 여러개 저장
     */
    public List<AttachmentRequestDto> storeFiles(List<MultipartFile> multipartFiles, AttachmentType attachmentType) throws IOException {
        List<AttachmentRequestDto> storeAttachmentResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeAttachmentResult.add(storeFile(multipartFile, attachmentType));
            }
        }
        return storeAttachmentResult;
    }

    /**
     * 파일 한 개 저장
     */
    public AttachmentRequestDto storeFile(MultipartFile multipartFile, AttachmentType attachmentType) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); //사용자가 저장한 파일명
        String storeFileName = createStoreFileName(originalFilename); //실제 저장할 파일명
        String filepath = createPath(storeFileName, attachmentType); //파일이 저장될 전체 경로

        //이미지 가로세로 크기
        int width = 0, height = 0;
        String contentType = multipartFile.getContentType();
        if (contentType!=null && contentType.startsWith("image/")) {
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
        }

        multipartFile.transferTo(new File(filepath));
        return AttachmentRequestDto.builder()
                .filename(storeFileName)
                .orgFilename(originalFilename)
                .filepath(filepath)
                .width(width)
                .height(height)
                .build();
    }

    //파일 경로 구성
    public String createPath(String storeFilename, AttachmentType attachmentType) {
        String viaPath = "";
        if (attachmentType == AttachmentType.IMAGE) {
            viaPath = "images/";
        } else if (attachmentType == AttachmentType.TEMP) {
            viaPath = "images/temporary/";
        } else if (attachmentType == AttachmentType.PROFILE) {
            viaPath = "profiles/";
        } else if (attachmentType == AttachmentType.VIDEO) {
            viaPath = "videos/";
        } else if (attachmentType == AttachmentType.DOCUMENT) {
            viaPath = "documents/";
        } else {
            viaPath = "generals/";
        }
        return fileDir + viaPath + storeFilename;
    }

    //저장할 파일 이름(랜덤값 부여)
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    /**
     * 첨부파일 저장 메소드
     */
//    public List<Attachment> saveAttachments(Map<AttachmentType, List<MultipartFile>> multipartFileListMap) throws IOException {
//        List<Attachment> imageFiles = fileStore.storeFiles(multipartFileListMap.get(AttachmentType.IMAGE), AttachmentType.IMAGE);
//        List<Attachment> generalFiles = fileStore.storeFiles(multipartFileListMap.get(AttachmentType.DOCUMENT), AttachmentType.DOCUMENT);
//        List<Attachment> result = Stream.of(imageFiles, generalFiles)
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
//
//        return result;
//    }
}
