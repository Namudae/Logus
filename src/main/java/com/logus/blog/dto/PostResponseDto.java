package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.blog.entity.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 정보를 리턴할 응답(Response) 클래스
 * Entity 클래스를 생성자 파라미터로 받아 데이터를 Dto로 변환하여 응답
 * 별도의 전달 객체를 활용해 연관관계를 맺은 엔티티간의 무한참조 방지
 */

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class PostResponseDto {

    private Long postId;
    private Long memberId;
    private String nickname;
    private Long categoryId;
    private String categoryName;
    private Long seriesId;
    private String seriesName;
    private String title;
    private String content;
    private Long views;
    private Status status;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private List<String> tags;
    private Long likeCount;
    private Long commentCount;
    private List<CommentResponseDto> comments;

    //이전게시글&다음게시글
    private String preTitle;
    private String nextTitle;

    /* Entity -> Dto */
    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.views = post.getViews();
        this.status = post.getStatus();
        this.createDate = post.getCreateDate();
    }

    public PostResponseDto(Post post, List<CommentResponseDto> comments, List<String> tags) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.views = post.getViews();
        this.status = post.getStatus();
        this.createDate = post.getCreateDate();
        this.comments = comments;
        this.tags = tags;
    }

    public PostResponseDto(List<CommentResponseDto> comments, List<String> tags) {
        this.comments = comments;
        this.tags = tags;
    }

    public PostResponseDto(Long postId) {
        this.postId = postId;
    }

}
