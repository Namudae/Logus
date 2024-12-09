package com.logus.blog.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class StatisticsMemberPostDto {
    private BlogMemberShortResponse memberInfo;
    private Long today;
    private Long total;
    private List<MemberPostDto> dateCount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberPostDto {
        private LocalDate date;
        private int postCount;
    }

    public StatisticsMemberPostDto(BlogMemberShortResponse memberShortResponse) {
        this.memberInfo = memberShortResponse;
    }
}
