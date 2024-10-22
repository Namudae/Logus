package com.logus.blog.service;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.repository.CommentRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.UserPrincipal;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    @Transactional
    public Long createComment(CommentRequestDto commentRequestDto) {

        Member member = memberService.getReferenceById(commentRequestDto.getMemberId());
        Post post = postRepository.getReferenceById(commentRequestDto.getPostId());
        Comment parent = commentRepository.getReferenceById(commentRequestDto.getParentId());

        Comment comment = commentRequestDto.toEntity(member, post, parent);
        commentRepository.save(comment);

        return comment.getId();

    }

    @Transactional
    public Long updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getById(commentId);
        comment.updateComment(commentRequestDto);
        return commentId;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getById(commentId);
        commentRepository.delete(comment);
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
