package com.logus.blog.service;

import com.logus.blog.repository.PostRepository;
import com.logus.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

//    @Test
//    @DisplayName("글 작성")
//    void createPostTest() {
//        //given
//        PostRequestDto postRequestDto = PostRequestDto.builder()
//                .blogId(1L)
//                .memberId(1L)
//                .categoryId(1L)
//                .seriesId(1L)
//                .title("제목입니다.")
//                .content("내용입니다.")
//                .build();
//
//        //when
//        postService.createPost(postRequestDto, null);
//
//        //then
//        assertEquals(3L, postRepository.count());
//        Post post = postRepository.findAll().get(2);
//        assertEquals("제목입니다.", post.getTitle());
//        assertEquals("내용입니다.", post.getContent());
//    }
//
//    @Test
//    @DisplayName("게시글 조회")
//    void selectPostTest() {
//        // given
//        PostRequestDto postRequestDto = PostRequestDto.builder()
//                .blogId(1L)
//                .memberId(1L)
//                .categoryId(1L)
//                .seriesId(1L)
//                .title("제목입니다.")
//                .content("내용입니다.")
//                .build();
//
//        // when
//        Long savedPostId = postService.createPost(postRequestDto, null);
//
//        // then
//        PostResponseDto actual = postService.selectPost("blog1", savedPostId);
//        Post expectedPost = postRepository.findAll().get(2);
//        assertEquals(expectedPost.getTitle(), "제목입니다.");
//        assertEquals(expectedPost.getContent(), "내용입니다.");
//
////        List<CommentResponseDto> comments = actual.getComments();
////        Assertions.assertTrue(!comments.isEmpty()); // You might need to check the comment logic in your service
//    }

}