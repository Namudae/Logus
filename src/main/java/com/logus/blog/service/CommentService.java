package com.logus.blog.service;

import com.logus.blog.dto.ChildCommentDto;
import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.ParentCommentDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.blog.repository.CommentRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.UserPrincipal;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public List<Comment> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public List<ParentCommentDto> getComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(ParentCommentDto::new)
                .toList();
    }

    public CommentResponseDto getParentChildComments(Long postId, boolean isMember) {
        // 모든 댓글 가져오기
        List<Comment> comments = commentRepository.findByPostId(postId);

        // 부모 댓글 생성
        List<ParentCommentDto> parents = comments.stream()
                .filter(comment -> comment.getParent() == null)
                .sorted(Comparator.comparing(Comment::getCreateDate))  // createDate 기준 내림차순 정렬
                .map(ParentCommentDto::new)
                .toList();

        // 자식 댓글 생성
        // 부모댓글이 notNull인 댓글을, parentId가 같은 것끼리 묶어서 반환
        List<ChildCommentDto> childComments = parents.stream()
                .map(parent -> {
                    List<ChildCommentDto.ChildDetailDto> childDetails = comments.stream()
                            .filter(comment -> comment.getParent() != null && comment.getParent().getId().equals(parent.getCommentId()))
                            .sorted(Comparator.comparing(Comment::getCreateDate))  // createDate 기준 내림차순 정렬
                            .map(childComment -> {
                                // 자식 댓글 DTO 생성
                                ChildCommentDto.ChildDetailDto dto = new ChildCommentDto.ChildDetailDto(childComment);

                                // 비밀 댓글 처리
                                if (!isMember && childComment.getStatus() == Status.SECRET) {
                                    dto.secretComment();
                                }

                                return dto;
                            })
                            .toList();
                    return new ChildCommentDto(parent.getCommentId(), childDetails);
                })
                .toList();


        // 댓글 데이터를 CommentResponseDto에 설정
        return CommentResponseDto.builder()
                .parents(parents)
                .childComments(childComments)
                .build();
    }

    @Transactional
    public Long createComment(CommentRequestDto commentRequestDto) {
        // memberId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(ErrorCode.NEED_LOGIN);
        }
        Long memberId = ((UserPrincipal) authentication.getPrincipal()).getMemberId();

        Member member = memberService.getReferenceById(memberId);
        Post post = postRepository.getReferenceById(commentRequestDto.getPostId());
        Comment parent = commentRepository.getReferenceById(commentRequestDto.getParentId());
//        commentRequestDto.setContent(CustomHtmlEscapeUtil.escapeCustom(commentRequestDto.getContent()));

        Comment comment = commentRequestDto.toEntity(member, post, parent);
        commentRepository.save(comment);

        return comment.getId();

    }

    @Transactional
    public Long updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getById(commentId);
//        commentRequestDto.setContent(CustomHtmlEscapeUtil.escapeCustom(commentRequestDto.getContent()));
        comment.updateComment(commentRequestDto);
        return commentId;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getById(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    public void bulkDeleteComment(Long postId) {
        commentRepository.bulkDeleteByPostId(postId);
    }


    //=====인가
    public boolean hasPermissionToComment(Long commentId, Authentication authentication) {
        // 로그인이 안 되어 있거나, 익명 사용자인 경우 예외 발생
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(ErrorCode.NEED_LOGIN);
        }

        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        var post = commentRepository.findById((Long) commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!post.getMember().getId().equals(userPrincipal.getMemberId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
        return true;
    }

}
