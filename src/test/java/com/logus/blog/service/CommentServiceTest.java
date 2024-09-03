package com.logus.blog.service;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.blog.repository.CommentRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;


    @Test
    @DisplayName("댓글 작성")
    void createCommentTest() {
        //given
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Post post = postRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .member(member)
                .post(post)
                .content("테스트 댓글입니다.")
                .status(Status.PUBLIC)
                .build();

        //when
        commentService.createComment(commentRequestDto);

        //then
        assertEquals(3L, commentRepository.count());
        Comment comment = commentRepository.findAll().get(2);
        assertEquals("테스트 댓글입니다.", comment.getContent());
        assertEquals(Status.PUBLIC, comment.getStatus());

    }

}