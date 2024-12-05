package com.logus.blog.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class VisitBlogDto {
    private List<BlogVisitorDto> blogVisitors;
    private Long today;
    private Long yesterday;
    private Long total;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BlogVisitorDto {
        private LocalDate date;
        private int visitCount;
    }
}
