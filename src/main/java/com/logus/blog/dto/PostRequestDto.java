package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostRequestDto {

        private Long postId;
        private Long memberId;
        private Long blogId;
        private Long categoryId;
        private Long seriesId;
        private String title;
        private String content;
        private Status status;
        //태그
        private List<String> tags;

        //Entity 대신 필요한값 받을것
//        private Member member;
//        private Blog blog;
//        private Category category;
        //private Series series;

        /*
        Dto -> toEntity
        Service에서 member 전달
         */
        public Post toEntity(Member member, Blog blog, Category category, Series series) {
            return Post.builder()
                    .id(postId)
                    .member(member)
                    .blog(blog)
                    .category(category)
                    .series(series)
                    .title(title)
                    .content(content)
                    .status(status)
                    .build();
        }

}