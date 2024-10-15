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

    public List<Comment> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public Long createComment(CommentRequestDto commentRequestDto) {

        Member member = memberService.getReferenceById(commentRequestDto.getMemberId());
        Post post = postRepository.getReferenceById(commentRequestDto.getPostId());
        Comment parent = commentRepository.getReferenceById(commentRequestDto.getParentId());

        Comment comment = commentRequestDto.toEntity(member, post, parent);
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
