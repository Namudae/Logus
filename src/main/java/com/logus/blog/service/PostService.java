package com.logus.blog.service;

import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Category;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Series;
import com.logus.blog.exception.PostNotFound;
import com.logus.blog.repository.*;
import com.logus.member.repository.MemberRepository;
import com.logus.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final SeriesRepository seriesRepository;
    private final TagRepository tagRepository;

    public List<PostResponseDto> selectAllPosts(String blogAddress) {
        return postRepository.selectAllPosts(blogAddress);
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto) {

        //고민..
        //1. findById > select로 인한 성능저하
        //2. getReferenceById > 데이터 검증x
        //findById
        Member member = memberRepository.findById(postRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        Blog blog = blogRepository.findById(postRequestDto.getBlogId())
                .orElseThrow(() -> new IllegalArgumentException("BLOG NOT FOUND"));
        //getReferenceById
        Category category = (postRequestDto.getCategoryId() == null) ? null :
                categoryRepository.getReferenceById(postRequestDto.getCategoryId());
        Series series = (postRequestDto.getSeriesId() == null) ? null :
                seriesRepository.getReferenceById(postRequestDto.getSeriesId());

        //태그 처리
        //1. tag 먼저 insert(인데 중복 체크)
        //2. postTag insert
//        tagRepository.existsByTagName(postRequestDto.getTags());

        Post post = postRequestDto.toEntity(member, blog, category, series);
        postRepository.save(post);

        return post.getId();
    }

    public PostResponseDto selectPost(String blogAddress, Long postId) {
        //게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        //댓글 조회
        List<CommentResponseDto> comments = commentRepository.findByPostId(postId)
                .stream()
                .map(CommentResponseDto::new)
                .toList();

        return new PostResponseDto(post, comments);
    }
}
