package com.logus.blog.dto;

import com.logus.blog.entity.Post;
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
    private String title;
    private String content;
    private Long count;
    private LocalDateTime createDate;
    private List<CommentResponseDto> comments;

    /* Entity -> Dto */
    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.count = post.getCount();
        this.createDate = post.getCreateDate();
//        this.comments = post.getComments().stream()
//                .map(CommentResponseDto::new)
//                .collect(Collectors.toList());
    }

    public PostResponseDto(Post post, List<CommentResponseDto> comments) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.nickname = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.count = post.getCount();
        this.createDate = post.getCreateDate();
        this.comments = comments;
    }

//    public PostResponseDto(Long postId, Long memberId, String nickname, String title, String content, Long count, LocalDateTime createDate, List<CommentResponseDto> comments) {
//        this.postId = postId;
//        this.memberId = memberId;
//        this.nickname = nickname;
//        this.title = title;
//        this.content = content;
//        this.count = count;
//        this.createDate = createDate;
//        this.comments = comments;
//    }
}