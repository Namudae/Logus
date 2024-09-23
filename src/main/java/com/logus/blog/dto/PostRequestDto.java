package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        private String title;

        @NotBlank(message = "내용을 입력하세요.")
        private String content;

        private Status status;

        // 각 태그에 특수문자를 허용하지 않는 패턴 설정
        private List<@Pattern(regexp = "^[^!@#$%^&*(),.?\":{}|<>]*$", message = "특수문자는 허용되지 않습니다.") String> tags;

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

//        public void validate() {
//                String specialCharacters = "[!@#$%^&*(),.?\":{}|<>]";
//                for (String tag : tags) {
//                        if (tag.matches(".*" + specialCharacters + ".*")) {
//                                throw new InvalidRequest("tags", "태그에 특수문자를 포함할 수 없습니다.");
//                        }
//                }
//        }

}