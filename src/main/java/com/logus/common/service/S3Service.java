package com.logus.common.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.logus.common.dto.AttachmentRequestDto;
import com.logus.common.entity.AttachmentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public static final String CLOUD_FRONT_DOMAIN_NAME = "https://df5lnwgipj4a5.cloudfront.net";

    /**
     * s3 임시폴더 업로드
     */
    public AttachmentRequestDto tempUpload(MultipartFile file) throws IOException {
        String storeFileName;
        try {
            String originalFilename = file.getOriginalFilename();
            storeFileName = createStoreFileName(originalFilename);
            String filepath = createPath(storeFileName, AttachmentType.TEMP);

            //이미지 가로세로 크기
            int width = 0, height = 0;
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, filepath, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            //Url
//            String url = amazonS3.getUrl(bucket, filepath).toString();
            String url = CLOUD_FRONT_DOMAIN_NAME + "/" + filepath;

        return AttachmentRequestDto.builder()
                .filename(storeFileName)
                .orgFilename(originalFilename)
                .filepath(url)
                .width(width)
                .height(height)
                .build();

        } catch (AmazonServiceException e) {
            log.error("AWS 서비스 오류: {}", e.getMessage());
            throw new IllegalStateException("파일 업로드에 실패했습니다.");
        } catch (SdkClientException e) {
            log.error("AWS 클라이언트 오류: {}", e.getMessage());
            throw new IllegalStateException("파일 업로드 중 문제가 발생했습니다.");
        }
    }

    /**
     * S3 썸네일 업로드
     */
    public String thumbUpload(MultipartFile file) throws IOException {
        String storeFileName;
        try {
            String originalFilename = file.getOriginalFilename();
            storeFileName = createStoreFileName(originalFilename);
            String filepath = createPath(storeFileName, AttachmentType.THUMB);

            //이미지 가로세로 크기
            int width = 0, height = 0;
            String contentType = file.getContentType();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, filepath, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            //Url
//            String url = amazonS3.getUrl(bucket, filepath).toString();
            String url = CLOUD_FRONT_DOMAIN_NAME + "/" + filepath;
            return filepath;

        } catch (AmazonServiceException e) {
            log.error("AWS 서비스 오류: {}", e.getMessage());
            throw new IllegalStateException("파일 업로드에 실패했습니다.");
        } catch (SdkClientException e) {
            log.error("AWS 클라이언트 오류: {}", e.getMessage());
            throw new IllegalStateException("파일 업로드 중 문제가 발생했습니다.");
        }
    }

    public void update(String oldSource, String newSource) {
        try {
            oldSource = URLDecoder.decode(oldSource, "UTF-8");
            newSource = URLDecoder.decode(newSource, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        moveS3(oldSource, newSource);
        deleteS3(oldSource);
    }


    //이미지 폴더로 이동(복사)
    private void moveS3(String oldSource, String newSource) {
        amazonS3.copyObject(bucket, oldSource, bucket, newSource);
    }

    //복사한 파일 temp 폴더에서 삭제
    public void deleteS3(String source) {
        amazonS3.deleteObject(bucket, source);
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

        //지정된 경로로 저장
//        multipartFile.transferTo(new File(filepath));
//        String url = upload(multipartFile);

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
            viaPath = AttachmentType.IMAGE.getPath() + "/";
        } else if (attachmentType == AttachmentType.TEMP) {
            viaPath = AttachmentType.TEMP.getPath() + "/";
        } else if (attachmentType == AttachmentType.THUMB) {
            viaPath = AttachmentType.THUMB.getPath() + "/";
        } else if (attachmentType == AttachmentType.PROFILE) {
            viaPath = AttachmentType.PROFILE.getPath() + "/";
        } else if (attachmentType == AttachmentType.VIDEO) {
            viaPath = AttachmentType.VIDEO.getPath() + "/";
        } else if (attachmentType == AttachmentType.DOCUMENT) {
            viaPath = AttachmentType.DOCUMENT.getPath() + "/";
        } else {
            viaPath = AttachmentType.OTHER.getPath() + "/";
        }
        return viaPath + storeFilename;
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


}
