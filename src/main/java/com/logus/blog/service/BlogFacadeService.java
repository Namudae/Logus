package com.logus.blog.service;

import com.logus.blog.dto.SeriesOrderRequestDto;
import com.logus.blog.dto.SeriesRequestDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Series;
import com.logus.blog.repository.*;
import com.logus.common.entity.AttachmentType;
import com.logus.common.security.UserPrincipal;
import com.logus.common.service.S3Service;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogFacadeService {

    private final PostService postService;
    private final BlogService blogService;
    private final SeriesService seriesService;
    private final S3Service s3Service;

    private final PostRepository postRepository;
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
        //시리즈 > imgUrl 서버에서 삭제
        seriesRepository.bulkDeleteByBlogId(blogId);
        //팔로우
        followRepository.bulkDeleteByBlogId(blogId);
        //방문
        visitRepository.bulkDeleteByBlogId(blogId);
        //블로그
        blogRepository.delete(blog);
        //블로그멤버
        blogMemberRepository.bulkDeleteByBlogId(blogId);
    }

    @Transactional
    public Long createSeries(SeriesRequestDto seriesRequestDto, MultipartFile img) throws IOException {
        Blog blog = blogService.getReferenceById(seriesRequestDto.getBlogId());

        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.thumbUpload(img, AttachmentType.SERIES);
        }

        Series series = seriesRequestDto.toEntity(blog, imgUrl);
        seriesRepository.save(series);
        return series.getId();
    }

    @Transactional
    public Long updateSeries(Long seriesId, SeriesRequestDto seriesRequestDto, MultipartFile img, boolean deleteImg) throws IOException {
        Series series = seriesService.getById(seriesId);

        //이미지 처리
        if (deleteImg) {
            //기존 썸네일 처리(s3 삭제, ImgUrl 지우기)
            s3Service.deleteS3(series.getImgUrl());
            series.deleteImgUrl();
        }
        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.thumbUpload(img, AttachmentType.SERIES);
        }

        series.updateSeries(seriesRequestDto, imgUrl);
        return seriesId;
    }

    public void deleteSeries(Long seriesId) {
        Series series = seriesService.getById(seriesId);
        //이미지 삭제
        if (series.getImgUrl() != null) {
            s3Service.deleteS3(series.getImgUrl());
        }
        seriesRepository.delete(series);
    }

    @Transactional
    public void updateSeriesOrder(SeriesOrderRequestDto seriesOrderRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        blogService.validateAuthentication(authentication);
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        for (SeriesOrderRequestDto.SeriesDto seriesDto : seriesOrderRequestDto.getSeriesList()) {
            Series series = seriesService.getById(seriesDto.getSeriesId());
            blogService.hasPermissionToBlogUserPrincipal(series.getId(), "SERIES", "ADMIN", userPrincipal);
            series.updateSeries(seriesDto);
        }

    }
}

