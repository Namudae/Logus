package com.logus.admin.service;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportRequest;
import com.logus.admin.entity.Report;
import com.logus.admin.repository.ReportRepository;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.service.CommentService;
import com.logus.blog.service.PostService;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReportRepository reportRepository;

    public Report getById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
    }

    public Report getReferenceById(Long reportId) {
        return reportId == null ? null :
                reportRepository.getReferenceById(reportId);
    }

    public ReportListResponseDto selectPostReports(Pageable pageable) {
        return reportRepository.selectPostReports(pageable);
    }

    public ReportListResponseDto selectCommentReports(Pageable pageable) {
        return reportRepository.selectCommentReports(pageable);
    }

    @Transactional
    public Long createReport(ReportRequest reportRequest) {
        //중복신고 불가능

        Member reporter = memberService.getById(memberService.authMemberId());
        Member reported = null;
        Comment comment = null;
        Post post = null;
        Integer count = 0;
        if (reportRequest.getCommentId() != null) {
            if (reportRepository.countByReporterIdAndCommentId(reporter.getId(), reportRequest.getCommentId())> 0) {
                throw new CustomException(ErrorCode.DUPLICATE_COMMENT_REPORT);
            }
            comment = commentService.getById(reportRequest.getCommentId());
            post = postService.getById(comment.getPost().getId());
            reported = comment.getMember();
            count = reportRepository.countByCommentId(reportRequest.getCommentId());
        } else {
            if (reportRepository.countByReporterIdAndPostId(reporter.getId(), reportRequest.getPostId())> 0) {
                throw new CustomException(ErrorCode.DUPLICATE_POST_REPORT);
            }
            post = postService.getById(reportRequest.getPostId());
            reported = post.getMember();
            count = reportRepository.countByPostId(reportRequest.getPostId());
        }
        Report report = reportRequest.toEntity(reporter, reported, post, comment);
        Long reportId = reportRepository.save(report).getId();

        //신고 누적n번일 경우 reportStatus.BLIND
        if (count >= 4) {
            if (reportRequest.getCommentId() != null) {
                reportRepository.bulkUpdateReportByCommentId(reportRequest.getCommentId());
                comment.blindComment();
            } else {
                reportRepository.bulkUpdateReportByPostId(post.getId());
                post.blindPost();
            }
        }

        return reportId;
    }
}
