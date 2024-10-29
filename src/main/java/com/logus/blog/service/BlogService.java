package com.logus.blog.service;

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
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final SeriesRepository seriesRepository;
    private final MemberService memberService;

    public Blog getById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new CustomException(ErrorCode.BLOG_NOT_FOUND));
    }

    public Blog getReferenceById(Long blogId) {
        return blogId == null ? null :
                blogRepository.getReferenceById(blogId);
    }

    @Transactional
    public Long createBlog(BlogRequestDto blogRequestDto) {
        Long memberId = authMemberId();

        Blog blog = blogRequestDto.toEntity();
        Blog savedBlog = blogRepository.save(blog);

        saveBlogMembers(blogRequestDto.getBlogMembers(), savedBlog, memberId);

        return savedBlog.getId();
    }

    @Transactional
    public Long updateBlog(Long blogId, BlogRequestDto blogRequestDto) {
        Blog blog = getById(blogId);
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

    public List<BlogMemberResponseDto> updateBlogAuth(Long blogId, List<BlogMemberRequestDto> blogMemberRequestDto) {
        Blog blog = getById(blogId);

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

    public List<SeriesResponseDto> selectSeries(Long blogId) {
        Blog blog = getById(blogId);
        return seriesRepository.findByBlogIdOrderBySeriesOrder(blog.getId()).stream()
                .map(SeriesResponseDto::new)
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

    private void validateBlogAuth(Blog blog, UserPrincipal userPrincipal, List<BlogAuth> allowAuths) {
        if (blog.getBlogMembers().stream()
                .filter(blogMember -> allowAuths.contains(blogMember.getBlogAuth())) // 허용된 권한에 해당하는 멤버 필터링
                .map(blogMember -> blogMember.getMember().getId())
                .noneMatch(ownerId -> ownerId.equals(userPrincipal.getMemberId()))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }

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

}
