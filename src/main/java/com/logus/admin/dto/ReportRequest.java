package com.logus.admin.dto;

import com.logus.admin.entity.Category;
import com.logus.admin.entity.Report;
import com.logus.admin.entity.ReportStatus;
import com.logus.admin.entity.ReportType;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.member.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReportRequest {

    private Long postId;
    private Long commentId;
    private Long reportedMemberId;
    private ReportType reportType;
    @Size(max=600, message = "신고 사유를 100자 이내로 작성해주세요")
    private String reason;

    /*
    Dto -> toEntity
     */
    public Report toEntity(Member reporter, Member reported, Post post, Comment comment) {
        if (this.reportType == null) {
            this.reportType = ReportType.OTHER;
        }
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .post(post)
                .comment(comment)
                .reportStatus(ReportStatus.PENDING)
                .reportType(reportType)
                .reason(reason)
                .build();
    }
}
