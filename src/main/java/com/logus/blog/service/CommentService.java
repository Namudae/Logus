package com.logus.blog.service;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.repository.CommentRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public Long createComment(CommentRequestDto commentRequestDto) {

        //수정전
        Member member = memberRepository.findById(commentRequestDto.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = postRepository.findById(commentRequestDto.getPost().getId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = commentRequestDto.toEntity(member, post);
        commentRepository.save(comment);

        return comment.getId();

    }
}
