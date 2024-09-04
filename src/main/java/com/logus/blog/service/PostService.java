package com.logus.blog.service;

import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.*;
import com.logus.blog.exception.PostNotFound;
import com.logus.blog.repository.*;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
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

    private final BlogService blogService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final SeriesService seriesService;
    private final TagService tagService;
    private final CommentService commentService;

    //+내용 130글자까지?
    public List<PostResponseDto> selectAllBlogPosts(String blogAddress) {
        return postRepository.selectAllBlogPosts(blogAddress);
    }

    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto) {

        //고민..
        //1. findById > select로 인한 성능저하
        //2. getReferenceById > 데이터 검증x
        //findById
        Member member = memberService.getById(postRequestDto.getMemberId()); //전부 replace
        Blog blog = blogService.getById(postRequestDto.getBlogId());
        //getReferenceById
        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //Post insert
        Post post = postRequestDto.toEntity(member, blog, category, series);
        Post savedPost = postRepository.save(post);

        //Tag insert
        if (postRequestDto.getTags() != null) {
            //1. tag insert
            List<Tag> tags = tagService.insertTags(postRequestDto.getTags());
            //2. postTag insert
            tagService.insertPostTags(savedPost, tags);
        }

        return post.getId();
    }


    public PostResponseDto selectPost(String blogAddress, Long postId) {
        //게시글 조회
        Post post = getById(postId);
        //댓글 조회
        List<CommentResponseDto> comments = commentService.getComments(postId);

        return new PostResponseDto(post, comments);
    }

}
