package com.logus.blog.dto;

import com.logus.blog.entity.Post;
import com.logus.member.entity.Member;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

//@Data
//@NoArgsConstructor

//@Getter
//@Setter
////@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
////@AllArgsConstructor
public class PostDto {

    /**
     * 등록, 수정을 처리할 요청(Request) 클래스
     */
    @Getter
    @Setter
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostRequest {
        private Long postId;
        private Long memberId;
        private String title;
        private String content;
        private LocalDateTime createDate;

        /*
        Dto -> toEntity
         */
        public Post toEntity(Member member) {
            return Post.builder()
                    .id(postId)
                    .member(member)
                    .title(title)
                    .content(content)
                    .build();
        }

        //등록 생성자
//        public PostRequest(Long id, Long memberId, String title, String content, LocalDateTime createDate) {
//            Id = id;
//            this.memberId = memberId;
//            this.title = title;
//            this.content = content;
//            this.createDate = createDate;
//        }


    }


    /**
     * 게시글 정보를 리턴할 응답(Response) 클래스
     * Entity 클래스를 생성자 파라미터로 받아 데이터를 Dto로 변환하여 응답
     * 별도의 전달 객체를 활용해 연관관계를 맺은 엔티티간의 무한참조 방지
     */
    @RequiredArgsConstructor
    @Getter
    @Data
    public static class PostResponse {
        private Long postId;
        private Long memberId;
        private String nickname;
        private String title;
        private String content;
        private Long count;
        private LocalDateTime createDate;

        /* Entity -> Dto */
        public PostResponse(Post post) {
            this.postId = post.getId();
            this.memberId = post.getMember().getId();
            this.nickname = post.getMember().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.count = post.getCount();
            this.createDate = post.getCreateDate();
        }
    }

//        @QueryProjection
//        public PostDto(Long memberId, Long postId, String title, String content, LocalDateTime createDate) {
//            this.memberId = memberId;
//            this.postId = postId;
//            this.title = title;
//            this.content = content;
//            this.createDate = createDate;
//        }

}