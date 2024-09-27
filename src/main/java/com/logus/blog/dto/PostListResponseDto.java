package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.common.service.S3Service;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class PostListResponseDto {

    private Long postId;
    private Long memberId;
    private String nickname;
    private Long categoryId;
    private String categoryName;
    private Long seriesId;
    private String seriesName;
    private String title;
    private String content;
    private String imgUrl;
    private Long views;
    private Status status;
    private ReportStatus reportStatus;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private List<String> tags;
    private Long likeCount;
    private Long commentCount;

    /* Entity -> Dto */
    public PostListResponseDto(Post post) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.views = post.getViews();
        this.createDate = post.getCreateDate();
    }

    public PostListResponseDto(Post post, List<CommentResponseDto> comments, List<String> tags) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.views = post.getViews();
        this.createDate = post.getCreateDate();
        this.tags = tags;
    }

    public PostListResponseDto(Long postId) {
        this.postId = postId;
    }

}
