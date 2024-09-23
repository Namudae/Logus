package com.logus.blog.service;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.repository.CommentRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public Long createComment(CommentRequestDto commentRequestDto) {

        Member member = memberService.getById(commentRequestDto.getMemberId());
        Post post = postRepository.findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRequestDto.toEntity(member, post);
        commentRepository.save(comment);

        return comment.getId();

    }

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentResponseDto::new)
                .toList();
    }

}
