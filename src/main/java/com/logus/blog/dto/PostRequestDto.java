package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

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

        @NotBlank(message = "제목을 입력하세요.")
        @Size(max=100, message = "제목의 최대 글자수는 100자입니다.")
        private String title;

        @NotBlank(message = "내용을 입력하세요.")
        @Size(max=10000, message = "본문의 최대 글자수는 10000자입니다.")
        private String content;

        private String imgUrl;

        private Status status;

        // 각 태그에 특수문자를 허용하지 않는 패턴 설정
        private List<@Pattern(regexp = "^[^!@#$%^&*(),.?\":{}|<>]*$", message = "특수문자는 허용되지 않습니다.") String> tags;

        public Post toEntity(Member member, Blog blog, Category category, Series series, String imgUrl) {
            return Post.builder()
                    .id(postId)
                    .member(member)
                    .blog(blog)
                    .category(category)
                    .series(series)
                    .title(title)
                    .content(content)
                    .imgUrl(imgUrl)
                    .status(status)
                    .build();
        }

        public Post toEntity(Member member, Blog blog, Category category, Series series, String imgUrl, Long postId) {
                return Post.builder()
                        .id(postId)
                        .member(member)
                        .blog(blog)
                        .category(category)
                        .series(series)
                        .title(title)
                        .content(content)
                        .imgUrl(imgUrl)
                        .status(status)
                        .build();
        }

        public Post toEntity(Category category, Series series) {
            return Post.builder()
                    .id(postId)
                    .category(category)
                    .series(series)
                    .title(title)
                    .content(content)
                    .status(status)
                    .build();
        }


}