package com.logus.blog.service;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.exception.PostNotFound;
import com.logus.member.repository.MemberRepository;
import com.logus.blog.repository.PostRepository;
import com.logus.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public List<PostResponseDto> selectAllPosts(String blogAddress) {
        return postRepository.selectAllPosts(blogAddress);
    }

    public Long createPost(PostRequestDto postRequestDto) {
        Long memberId = postRequestDto.getMember().getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = postRequestDto.toPostEntity(member);
        postRepository.save(post);

        return post.getId();
    }

    public PostResponseDto selectPost(String blogAddress, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        //수정중
        return PostResponseDto.builder()
                .memberId(post.getMember().getId())
                .nickname(post.getMember().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .count(post.getCount())
                .createDate(post.getCreateDate())
                .build();
    }
}
