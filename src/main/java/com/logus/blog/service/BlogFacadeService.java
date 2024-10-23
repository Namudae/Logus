package com.logus.blog.service;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Post;
import com.logus.blog.repository.*;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogFacadeService {

    private final PostService postService;
    private final PostRepository postRepository;
    private final BlogService blogService;
    private final SeriesRepository seriesRepository;
    private final FollowRepository followRepository;
    private final VisitRepository visitRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final BlogRepository blogRepository;

    //블로그 삭제
    // - 게시글
    // - 시리즈
    // - 팔로우
    // - 방문
    // - 블로그멤버
    // - 블로그
    @Transactional
    public void deleteBlog(Long blogId) {
        Blog blog = blogService.getById(blogId);

        //게시글
        List<Post> posts = postRepository.findByBlogId(blogId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //시리즈
        seriesRepository.bulkDeleteByBlogId(blogId);
        //팔로우
        followRepository.bulkDeleteByBlogId(blogId);
        //방문
        visitRepository.bulkDeleteByBlogId(blogId);
        //블로그멤버
        blogMemberRepository.bulkDeleteByBlogId(blogId);
        //블로그
        blogRepository.delete(blog);
    }
}
