package com.logus.blog.controller;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.service.PostService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 블로그의 전체게시글 조회
     * + Pageable x
     */
//    @GetMapping("/{blogAddress}/posts")
//    public List<PostResponseDto> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress) {
//        return postService.selectAllBlogPosts(blogAddress);
//    }

    /**
     * 블로그의 전체게시글 조회
     * + Pageable
     */
    @GetMapping("/{blogAddress}/posts")
    public ApiResponse<Page<PostResponseDto>> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress, Pageable pageable) {
        Page<PostResponseDto> posts = postService.selectAllBlogPosts(blogAddress, pageable);
//        return new ResponseEntity<>(posts, HttpStatus.OK);

        //커스텀(body에 status 포함)
        return ApiResponse.of(HttpStatus.OK, posts);
    }

    /**
     * 블로그 내부 검색(제목+내용)
     * + Pageable
     * + sort 추가?
     * http://localhost:8082/blog1/search?page=0&size=10&keyword=1번째
     */
    @GetMapping("/{blogAddress}/search")
    public ApiResponse<Page<PostResponseDto>> searchBlogPosts(@PathVariable("blogAddress") String blogAddress,
                                                                 @RequestParam(value="keyword", required = false) String keyword,
                                                                 Pageable pageable) {
        Page<PostResponseDto> pagePosts = postService.searchBlogPosts(blogAddress, keyword, pageable);

//        return new ResponseEntity<>(pagePosts, HttpStatus.OK);

        return ApiResponse.of(HttpStatus.OK, "성공", pagePosts);
    }

    /**
     * 글 한개 조회
     */
    @GetMapping("/{blogAddress}/{postId}")
    public ApiResponse<PostResponseDto> selectPost(@PathVariable("blogAddress") String blogAddress,
                                      @PathVariable("postId") Long postId) {
//        return postService.selectPost(blogAddress, postId);

        return ApiResponse.of(HttpStatus.OK, "성공", postService.selectPost(blogAddress, postId));
    }

    /**
     * 글 등록(임시)
     */
    @PostMapping("/{blogAddress}/postv1")
    public String createPostV1(@PathVariable("blogAddress") String blogAddress,
                             @RequestBody PostRequestDto postRequestDto) {
        Long postId = postService.createPost(postRequestDto);
        return "redirect:/" + blogAddress + "/" + postId;
    }

    /**
     * 글 등록(+사진 첨부)
     */
    @PostMapping("/{blogAddress}/post")
    public String createPost(@PathVariable("blogAddress") String blogAddress,
                             @RequestPart("requestDto") @Valid PostRequestDto postRequestDto
//                             @RequestPart("images") MultipartFile[] images,
//                             @RequestPart("thumbImg") MultipartFile thumbImage
    ) throws MethodArgumentNotValidException {
//        postRequestDto.validate();
        Long postId = postService.createPost(postRequestDto);
        return "redirect:/" + blogAddress + "/" + postId;
    }

    /**
     * 글 수정
     */

    /**
     * 글 삭제
     *  - 댓글 delYn 처리
     *  - postTag delete
     *  - attachment db delete(s3에서도 삭제)
     */


}
    //검색 참고
//    @GetMapping("/search")
//    public ResponseEntity searchTitle(@RequestParam(value ="title",required = false) String title,
//                                      @RequestParam @Positive int page,
//                                      @RequestParam @Positive int size){
//        Page<Post> pagePosts = postService.searchByTitle(title, page, size);
//        List<Post> questions = pagePosts.getContent();
//
//        //return new ResponseEntity<>(new MultiResponseDto<>(mapper.questionToQuestionResponseDtos(questions),pageQuestions), HttpStatus.OK);
//
//        return new ResponseEntity<>(new PostResponseDto(), HttpStatus.OK);
//    }
