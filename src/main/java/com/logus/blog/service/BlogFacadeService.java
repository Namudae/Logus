package com.logus.blog.service;

import com.logus.blog.dto.*;
import com.logus.blog.entity.*;
import com.logus.blog.repository.*;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.UserPrincipal;
import com.logus.common.service.S3Service;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogFacadeService {

    private final PostService postService;
    private final BlogService blogService;
    private final MemberService memberService;
    private final SeriesService seriesService;
    private final S3Service s3Service;

    private final PostRepository postRepository;
    private final SeriesRepository seriesRepository;
    private final FollowRepository followRepository;
    private final VisitRepository visitRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final BlogRepository blogRepository;

    //블로그 탈퇴시 사용
    // - 게시글O
    // - 시리즈O
    // - 팔로우O
    // - 블로그멤버O
    // - 블로그O
    // - 방문?
    @Transactional
    public void deleteBlog(Long blogId) {
        Blog blog = blogService.getById(blogId);

        //게시글
        List<Post> posts = postRepository.findByBlogId(blogId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //시리즈
        List<Series> series = seriesRepository.findByBlogIdOrderBySeriesOrder(blogId);
        for (Series s : series) {
            if (s.getImgUrl() != null || !s.getImgUrl().equals("")) {
                s3Service.deleteS3(s.getImgUrl());
            }
        }
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

    //블로그 삭제시 사용
    @Transactional
    public void resetBlog(Long blogId) {
        Blog blog = blogService.getById(blogId);

        //게시글
        List<Post> posts = postRepository.findByBlogId(blogId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //시리즈
        List<Series> series = seriesRepository.findByBlogIdOrderBySeriesOrder(blogId);
        for (Series s : series) {
            if (s.getImgUrl() != null || !s.getImgUrl().equals("")) {
                s3Service.deleteS3(s.getImgUrl());
            }
        }
        seriesRepository.bulkDeleteByBlogId(blogId);
        //팔로우
        followRepository.bulkDeleteByBlogId(blogId);
        //방문
        visitRepository.bulkDeleteByBlogId(blogId);
        if (blog.getShareYn()=="Y") {
            //블로그
            blogRepository.delete(blog);
            //블로그멤버
            blogMemberRepository.bulkDeleteByBlogId(blogId);
        }
    }

    @Transactional
    public Long createSeries(SeriesRequestDto seriesRequestDto, MultipartFile img) throws IOException {
        Blog blog = blogService.getReferenceById(seriesRequestDto.getBlogId());

        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.imgUpload(img, AttachmentType.SERIES);
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
            imgUrl = s3Service.imgUpload(img, AttachmentType.SERIES);
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


    public Page<FollowResponseDto> selectFollow(Pageable pageable) {
        Long memberId = blogService.authMemberId();

        return blogRepository.selectFollows(memberId, pageable);
    }

    public void createFollow(Long blogId) {
        Long memberId = blogService.authMemberId();

        Blog blog = blogService.getById(blogId);
        Member member = memberService.getById(memberId);

        Follow follow = Follow.builder()
                .blog(blog)
                .member(member)
                .build();
        followRepository.save(follow);
    }

    public void deleteFollow(Long followId) {
        Long memberId = blogService.authMemberId();
        Follow follow = followRepository.getReferenceById(followId);
        if (follow.getMember().getId() != memberId) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        followRepository.delete(follow);
    }

    public Page<FollowerResponseDto> selectFollower(Long blogId, Pageable pageable) {
        Page<FollowerResponseDto> followers = blogRepository.selectFollowers(blogId, pageable);

        // 각 FollowerResponseDto의 imgUrl을 가공
        followers.forEach(FollowerResponseDto::processImgUrl);

        return followers;
    }

    public void deleteFollower(Long followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        blogService.validateAuthentication(authentication);
        Follow follow = followRepository.getReferenceById(followId);
        blogService.hasPermissionToBlog(follow.getBlog().getId(), "BLOG", "ADMIN", authentication);
        followRepository.delete(follow);
    }

    public List<OurLogResponseDto> selectOurLog() {
        Long memberId = blogService.authMemberId();
        List<OurLogResponseDto> dtos = blogRepository.findByMemberId(memberId);

        // 각 OurLogResponseDto의 imgUrl을 가공
        dtos.forEach(OurLogResponseDto::changeImgUrl);
        return dtos;
    }


    @Transactional
    public Long updateBlog(Long blogId, BlogRequestDto blogRequestDto) {
        Blog blog = blogService.getById(blogId);
        //블로그 멤버
        //oldMember 중 new에 없는 멤버 delete
        List<BlogMember> oldBlogMembers = blogMemberRepository.findByBlogId(blogId);
        List<BlogMemberRequestDto> newBlogMembers = blogRequestDto.getBlogMembers();

        // 기존 블로그 멤버 ID
        Set<Long> newMemberIds = newBlogMembers.stream()
                .map(BlogMemberRequestDto::getMemberId)
                .collect(Collectors.toSet());

        // 1. 기존 멤버 삭제: newBlogMembers에 없는 oldBlogMembers 삭제
        oldBlogMembers.stream()
                .filter(oldMember -> !newMemberIds.contains(oldMember.getMember().getId())
                        && !oldMember.getBlogAuth().equals(BlogAuth.OWNER)) //소유자 삭제X
                .forEach(blogMemberRepository::delete);

        // 2. 새로운 멤버 추가: 기존에 없는 멤버는 새로 추가
        newBlogMembers.stream()
                .filter(newMember -> oldBlogMembers.stream()
                        .noneMatch(oldMember -> oldMember.getMember().getId().equals(newMember.getMemberId())))
                .forEach(newMember -> {
                    Member member = memberService.getById(newMember.getMemberId());
                    BlogMember blogMember = newMember.toEntity(member, blog, BlogAuth.EDITOR);
                    blogMemberRepository.save(blogMember);
                });
        blog.updateBlogInfo(blogRequestDto);
        return blogId;
    }

    public List<BlogMemberResponseDto> updateBlogAuth(Long blogId, List<BlogMemberRequestDto> blogMemberRequestDto) {
        Blog blog = blogService.getById(blogId);

        blogMemberRequestDto.stream()
                .filter(updateMember -> updateMember.getBlogAuth() != BlogAuth.OWNER)  // OWNER 제외
                .forEach(updateMember -> {
                    Member member = memberService.getById(updateMember.getMemberId());
                    BlogMember existingMember = blogMemberRepository.findByBlogAndMember(blog, member);

                    if (existingMember != null && existingMember.getBlogAuth() != BlogAuth.OWNER) {
                        // 기존 멤버의 blogAuth가 OWNER가 아닌 경우에만 업데이트 수행
                        existingMember.updateBlogAuth(updateMember.getBlogAuth());
                        blogMemberRepository.save(existingMember);
                    }
                });

        return null;
    }

    @Transactional
    public Long createBlog(BlogRequestDto blogRequestDto) {
        Long memberId = blogService.authMemberId();

        Blog blog = blogRequestDto.toEntity();
        Blog savedBlog = blogRepository.save(blog);

        saveBlogMembers(blogRequestDto.getBlogMembers(), savedBlog, memberId);

        return savedBlog.getId();
    }

    @Transactional
    private void saveBlogMembers(List<BlogMemberRequestDto> blogMembers, Blog savedBlog, Long ownerId) {
        if (blogMembers != null) {
            for (BlogMemberRequestDto blogMemberRequestDto : blogMembers) {
                Member member = memberService.getById(blogMemberRequestDto.getMemberId());
                BlogAuth blogAuth = null;
                if (blogMemberRequestDto.getMemberId() == ownerId) {
                    blogAuth = BlogAuth.OWNER;
                } else {
                    blogAuth = BlogAuth.EDITOR;
                }
                BlogMember blogMember = blogMemberRequestDto.toEntity(member, savedBlog, blogAuth);
                blogMemberRepository.save(blogMember);
            }
        }
    }
}

