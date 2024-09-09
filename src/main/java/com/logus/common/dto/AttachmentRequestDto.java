package com.logus.common.dto;

import com.logus.common.entity.AttachmentType;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequestDto {

//    private AttachmentType attachmentType;
    private String filename;
    private String orgFilename;
    private String filepath;
    private int width;
    private int height;

//    @Builder
//    public AttachmentRequestDto(AttachmentType attachmentType, String filename, String orgFilename, String filepath) {
//        this.attachmentType = attachmentType;
//        this.filename = filename;
//        this.orgFilename = orgFilename;
//        this.filepath = filepath;
//    }

    //게시글이 생성된 후에 처리되어야함. 생성된 게시글 ID를 파일 요청 객체의 postId에 저장하는 용도로 사용
//    public void setPostId(Long postId) {
//        this.postId = postId;
//    }

}
