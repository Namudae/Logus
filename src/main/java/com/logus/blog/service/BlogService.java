package com.logus.blog.service;

import com.logus.admin.dto.BlogListResponseDto;
import com.logus.blog.dto.*;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.BlogMember;
import com.logus.blog.entity.Series;
import com.logus.blog.repository.*;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.UserPrincipal;
import com.logus.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final SeriesRepository seriesRepository;

    public Blog getById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
    }

    public Blog getReferenceById(Long blogId) {
        return blogId == null ? null :
                blogRepository.getReferenceById(blogId);
    }


    public BlogResponseDto selectBlogInfo(Long blogId) {
        //블로그, 블로그멤버, 시리즈 따로따로
        Blog blog = getById(blogId);

        //블로그멤버
        List<BlogMemberResponseDto> blogMembers = selectBlogAuth(blogId);

        return new BlogResponseDto(blog, blogMembers);
    }


    public List<BlogMemberResponseDto> selectBlogAuth(Long blogId) {
        return blogMemberRepository.findByBlogId(blogId).stream()
                .map(blogMember -> {
                    Member member = blogMember.getMember();
                    String myLogAddress = blogRepository.findMyLogAddress(member.getId());
                    String imgUrl = member.getImgUrl();
                    if (imgUrl != null) {
                        imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
                    }
                    return BlogMemberResponseDto.builder()
                            .memberId(member.getId())
                            .nickname(member.getNickname())
                            .blogAuth(blogMember.getBlogAuth())
                            .imgUrl(imgUrl)
                            .myLogAddress(myLogAddress)
                            .createDate(blogMember.getCreateDate())
                            .build();
                })
                .toList();

    }

    public List<SeriesListResponseDto> selectSeries(Long blogId) {
        Blog blog = getById(blogId);
        return seriesRepository.findByBlogIdOrderBySeriesOrder(blog.getId()).stream()
                .map(series -> {
                    String imgUrl = series.getImgUrl();
                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = null;
                    } else {
                        imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
                    }
                    return new SeriesListResponseDto(series, imgUrl);
                        })
                .toList();
    }

    public Long getBlogIdByAddress(String blogAddress) {
        return blogRepository.findByBlogAddress(blogAddress)
                .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND)).getId();
    }

    public boolean isMember(Long loginId, List<Long> blogMemberIds) {
        if (loginId == null) {
            return false;
        }
        return blogMemberIds.stream()
                .anyMatch(memberId -> memberId.equals(loginId));
    }

    public List<Long> blogMemberIds(Long blogId) {
        return blogMemberRepository.findByBlogId(blogId).stream()
                .map(blogMember -> blogMember.getMember().getId())
                .collect(toList());
    }

    public void duplicateBlogAddress(String blogAddress) {
        if (blogRepository.existsByBlogAddress(blogAddress)) {
            throw new CustomException(ErrorCode.DUPLICATE_BLOG_ADDRESS);
        }
    }
    
    @Transactional
    public Blog registerBlog(Member member, BlogRequestDto blogRequestDto) {

        Blog blog = blogRequestDto.toEntity();
        Blog savedBlog = blogRepository.save(blog);

        //블로그멤버
        BlogMember blogMember = BlogMember.builder()
                .blogAuth(BlogAuth.OWNER)
                .member(member)
                .blog(blog)
                .build();
        blogMemberRepository.save(blogMember);

        return savedBlog;
    }

    //==========인가==========

    //블로그 관리자 검증
    public boolean hasPermissionToBlog(Long targetId, String targetType, String blogAuth, Authentication authentication) {
        // 로그인이 안 되어 있거나, 익명 사용자인 경우 예외 발생
        validateAuthentication(authentication);
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<BlogAuth> allowedAuths = new ArrayList<>();
        // blogAuth에 따라 허용할 권한 목록 설정
        switch (blogAuth) {
            case "OWNER":
                allowedAuths.add(BlogAuth.OWNER);
                break;
            case "ADMIN":
                allowedAuths.add(BlogAuth.OWNER);
                allowedAuths.add(BlogAuth.ADMIN);
                break;
            case "EDITOR":
                allowedAuths.add(BlogAuth.OWNER);
                allowedAuths.add(BlogAuth.ADMIN);
                allowedAuths.add(BlogAuth.EDITOR);
                break;
        }

        //targetType: SERIES
        if (Objects.equals(targetType, "SERIES")) {
            Series series = seriesRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SERIES_NOT_FOUND));
            var blog = blogRepository.findById(series.getBlog().getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
            validateBlogAuth(blog, userPrincipal, allowedAuths);
        }

        //targetType: BLOG
        if (Objects.equals(targetType, "BLOG")) {
            var blog = blogRepository.findById((Long) targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
            validateBlogAuth(blog, userPrincipal, allowedAuths);
        }

        return true;
    }


    public boolean hasPermissionToBlogUserPrincipal(Long targetId, String targetType, String blogAuth, UserPrincipal userPrincipal) {

        List<BlogAuth> allowedAuths = new ArrayList<>();
        // blogAuth에 따라 허용할 권한 목록 설정
        switch (blogAuth) {
            case "OWNER":
                allowedAuths.add(BlogAuth.OWNER);
                break;
            case "ADMIN":
                allowedAuths.add(BlogAuth.OWNER);
                allowedAuths.add(BlogAuth.ADMIN);
                break;
            case "EDITOR":
                allowedAuths.add(BlogAuth.OWNER);
                allowedAuths.add(BlogAuth.ADMIN);
                allowedAuths.add(BlogAuth.EDITOR);
                break;
        }

        //targetType: SERIES
        if (Objects.equals(targetType, "SERIES")) {
            Series series = seriesRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SERIES_NOT_FOUND));
            var blog = blogRepository.findById(series.getBlog().getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
            validateBlogAuth(blog, userPrincipal, allowedAuths);
        }

        //targetType: BLOG
        if (Objects.equals(targetType, "BLOG")) {
            var blog = blogRepository.findById((Long) targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
            validateBlogAuth(blog, userPrincipal, allowedAuths);
        }

        return true;
    }

    public Boolean isBlogMember(Blog blog, Long memberId) {
        return blog.getBlogMembers().stream()
                .filter(blogMember -> blogMember.getBlogAuth() == BlogAuth.OWNER || blogMember.getBlogAuth() == BlogAuth.ADMIN) // OWNER 또는 ADMIN 필터링
                .map(blogMember -> blogMember.getMember().getId()) // 멤버 ID 추출
                .anyMatch(ownerId -> ownerId.equals(memberId)); // 현재 사용자 ID와 일치 여부 확인
    }

    private void validateBlogAuth(Blog blog, UserPrincipal userPrincipal, List<BlogAuth> allowAuths) {
        if (blog.getBlogMembers().stream()
                .filter(blogMember -> allowAuths.contains(blogMember.getBlogAuth())) // 허용된 권한에 해당하는 멤버 필터링
                .map(blogMember -> blogMember.getMember().getId())
                .noneMatch(ownerId -> ownerId.equals(userPrincipal.getMemberId()))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }

    //로그인 필요
    public void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CustomException(ErrorCode.NEED_LOGIN);
        }
    }

    public Long authMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(authentication);
        return ((UserPrincipal) authentication.getPrincipal()).getMemberId();
    }

    public Long authMemberIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return ((UserPrincipal) authentication.getPrincipal()).getMemberId();
    }

    public Page<BlogListResponseDto> searchBlogs(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        return blogRepository.searchBlogs(loginId, nickname, blogName, blogAddress, pageable);
    }
}
