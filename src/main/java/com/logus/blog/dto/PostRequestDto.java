package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

//@Data
//@NoArgsConstructor

//@Getter
//@Setter
////@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
////@AllArgsConstructor

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {

    /**
     * 등록, 수정을 처리할 요청(Request) 클래스
     */
        private Long postId;
        private String title;
        private String content;
        private LocalDateTime createDate;
        private Member member;
        private Blog blog;
        private Category category;
        private Series series;
        private Status status;
        //태그추가

        /*
        Dto -> toEntity
        Service에서 member 전달
         */
        public Post toPostEntity(Member member) {
            return Post.builder()
                    .id(postId)
                    .member(member)
                    .blog(blog)
                    .category(category)
                    .series(series)
                    .title(title)
                    .content(content)
                    .status(status)
                    .createDate(createDate)
                    .build();
        }

        //등록 생성자
//        public PostRequestDto(Long id, Long memberId, String title, String content, LocalDateTime createDate) {
//            Id = id;
//            this.memberId = memberId;
//            this.title = title;
//            this.content = content;
//            this.createDate = createDate;
//        }




}