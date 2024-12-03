package com.logus.admin.service;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportRequest;
import com.logus.admin.entity.Report;
import com.logus.admin.entity.ReportStatus;
import com.logus.admin.repository.ReportRepository;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.service.CommentService;
import com.logus.blog.service.PostService;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ReportListResponseDto responseDto = reportRepository.selectPostReports(pageable);

        responseDto.getReportList().forEach(dto -> {
            if ((dto.getReportStatus().equals(ReportStatus.DELETE))) {
                dto.setPostTitle("삭제 처리 된 게시글입니다.");
            } else if (dto.getPostTitle()==null) {
                dto.setPostTitle("삭제된 게시글입니다.");
            }
        });
        return responseDto;
    }

    public ReportListResponseDto selectCommentReports(Pageable pageable) {
        ReportListResponseDto responseDto = reportRepository.selectCommentReports(pageable);

        responseDto.getReportList().forEach(dto -> {
            if ((dto.getReportStatus().equals(ReportStatus.DELETE))) {
                dto.setCommentContent("삭제 처리 된 댓글입니다.");
            } else if (dto.getCommentContent()==null) {
                dto.setCommentContent("삭제된 댓글입니다.");
            }
        });
        return responseDto;
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

    @Transactional
    public void handlePostReport(Long reportId, ReportStatus reportStatus) {
        Report report = getById(reportId);
        Post post = postService.getById(report.getPost().getId());
        if (reportStatus.equals(ReportStatus.BLOCK)) {
            report.updateReportStatus(ReportStatus.BLOCK);
            post.blockPost();
        } else if (reportStatus.equals(ReportStatus.DELETE)) {
            report.updateReportStatus(ReportStatus.DELETE);
            postService.deletePost(post.getId());
        } else if (reportStatus.equals(ReportStatus.RETURN)) {
            report.updateReportStatus(ReportStatus.RETURN);
        }
    }

    @Transactional
    public void handleCommentReport(Long reportId, ReportStatus reportStatus) {
        Report report = getById(reportId);
        Comment comment = commentService.getById(report.getComment().getId());
        if (reportStatus.equals(ReportStatus.BLOCK)) {
            report.updateReportStatus(ReportStatus.BLOCK);
            comment.blockComment();
        } else if (reportStatus.equals(ReportStatus.DELETE)) {
            report.updateReportStatus(ReportStatus.DELETE);
            commentService.deleteComment(comment.getId());
        } else if (reportStatus.equals(ReportStatus.RETURN)) {
            report.updateReportStatus(ReportStatus.RETURN);
        }
    }

    public ReportListResponseDto selectPostReport(Long reportId) {
        return null;
    }
}

