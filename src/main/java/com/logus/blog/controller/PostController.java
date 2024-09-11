package com.logus.blog.controller;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 글 여러개 조회
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
    public ResponseEntity<Page<PostResponseDto>> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress, Pageable pageable) {
        Page<PostResponseDto> posts = postService.selectAllBlogPosts(blogAddress, pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 블로그 내부 검색(제목+내용)
     * + Pageable
     * + sort 추가?
     * http://localhost:8082/blog1/search?page=0&size=10&keyword=1번째
     */
    @GetMapping("/{blogAddress}/search")
    public ResponseEntity<Page<PostResponseDto>> searchBlogPosts(@PathVariable("blogAddress") String blogAddress,
                                                                 @RequestParam(value="keyword", required = false) String keyword,
                                                                 Pageable pageable) {
        Page<PostResponseDto> pagePosts = postService.searchBlogPosts(blogAddress, keyword, pageable);
        return new ResponseEntity<>(pagePosts, HttpStatus.OK);
    }

    /**
     * 글 한개 조회
     */
    @GetMapping("/{blogAddress}/{postId}")
    public PostResponseDto selectPost(@PathVariable("blogAddress") String blogAddress,
                                      @PathVariable("postId") Long postId) {
        return postService.selectPost(blogAddress, postId);
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
                             @RequestPart("requestDto") PostRequestDto postRequestDto
//                             @RequestPart("images") MultipartFile[] images,
//                             @RequestPart("thumbImg") MultipartFile thumbImage
    ) throws IOException {

//        if (images != null) {
//            for (MultipartFile image : images) {
////                String fullPath = fileDir + image.getOriginalFilename(); //file.getOriginalFilename: 사용자가 업로드한 파일명
////                image.transferTo(new File(fullPath)); //file.transferto(): 파일저장
//
//                //images > s3 temp 폴더에서 images 폴더로 복제 후 삭제, db insert
//                //thumbImg > s3 thumb 폴더에 postid로 저장하고 url createPost로 전달, db url컬럼에 저장
//            }
//        }

        Long postId = postService.createPost(postRequestDto);
        return "redirect:/" + blogAddress + "/" + postId;
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

}
