package com.logus.blog.service;

import com.logus.blog.dto.*;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogMember;
import com.logus.blog.repository.BlogMemberRepository;
import com.logus.blog.repository.BlogRepository;
import com.logus.blog.repository.SeriesRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogMemberRepository blogMemberRepository;
//    private final MemberRepository memberRepository;
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
        Blog blog = blogRequestDto.toEntity();
        Blog savedBlog = blogRepository.save(blog);

        saveBlogMembers(blogRequestDto.getBlogMembers(), savedBlog);

        return savedBlog.getId();
    }

    @Transactional
    public Long updateBlog(Long blogId, BlogRequestDto blogRequestDto) {
        Blog blog = getById(blogId);
        //블로그 멤버
        //oldMember 중 new에 없는 멤버 delete
        List<BlogMember> oldBlogMembers = blogMemberRepository.findByBlogId(blogId);
        List<BlogMemberRequestDto> newBlogMembers = blogRequestDto.getBlogMembers();

        // 기존 블로그 멤버를 ID로 매핑하여 쉽게 찾을 수 있도록 함
        Set<Long> newMemberIds = newBlogMembers.stream()
                .map(BlogMemberRequestDto::getMemberId)
                .collect(Collectors.toSet());

        // 1. 기존 멤버 삭제: newBlogMembers에 없는 oldBlogMembers 삭제
        oldBlogMembers.stream()
                .filter(oldMember -> !newMemberIds.contains(oldMember.getMember().getId()))
                .forEach(blogMemberRepository::delete);

        // 2. 새로운 멤버 추가 및 기존 멤버 업데이트
        newBlogMembers.forEach(newMember -> {
            BlogMember existingMember = oldBlogMembers.stream()
                    .filter(oldMember -> oldMember.getMember().getId().equals(newMember.getMemberId()))
                    .findFirst()
                    .orElse(null);

            if (existingMember != null) {
                // 기존 멤버의 blogAuth 업데이트
                existingMember.updateBlogAuth(newMember.getBlogAuth());
            } else {
                // 새로운 멤버 추가
                Member member = memberService.getById(newMember.getMemberId());
                BlogMember blogMember = newMember.toEntity(member, blog);
                blogMemberRepository.save(blogMember);
            }
        });

//        oldBlogMembers.stream()
//                .filter(oldMember -> newBlogMembers.stream()
//                        .noneMatch(newMember -> newMember.getMemberId().equals(oldMember.getMember().getId())))
//                .forEach(blogMemberRepository::delete);
//
//        //newMember 중 old에 없는 멤버 insert
//        newBlogMembers.stream()
//                .filter(newMember -> oldBlogMembers.stream()
//                        .noneMatch(oldMember -> oldMember.getMember().getId().equals(newMember.getMemberId())))
//                .forEach(newMember -> {
//                    // Member 가져오기
//                    Member member = memberService.getById(newMember.getMemberId());
//                    // BlogMember 엔티티로 변환하여 저장
//                    BlogMember blogMember = newMember.toEntity(member, blog);
//                    blogMemberRepository.save(blogMember);
//                });

        blog.updateBlogInfo(blogRequestDto);

        return blogId;
    }

    private void saveBlogMembers(List<BlogMemberRequestDto> blogMembers, Blog savedBlog) {
        if (blogMembers != null) {
            for (BlogMemberRequestDto blogMemberRequestDto : blogMembers) {
                Member member = memberService.getById(blogMemberRequestDto.getMemberId());
                BlogMember blogMember = blogMemberRequestDto.toEntity(member, savedBlog);
                blogMemberRepository.save(blogMember);
            }
        }
    }

    public BlogResponseDto selectBlogInfo(Long blogId) {
        //블로그, 블로그멤버, 시리즈 따로따로
        Blog blog = getById(blogId);

        //블로그멤버
        //memberId > myLogAddress: memberId로 BlogMember 검색 > 블로그 권한 'OWNER'만 Blog에서 검색 > 공유블로그여부 'N'만 찾기
        List<BlogMemberResponseDto> blogMembers = blogMemberRepository.findByBlogId(blogId).stream()
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
                            .build();
                })
                .toList();

        return new BlogResponseDto(blog, blogMembers);
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
//        return blogMemberRepository.findMemberIdByBlogId(blogId);
        return blogMemberRepository.findByBlogId(blogId).stream()
                .map(blogMember -> blogMember.getMember().getId())
                .collect(toList());
    }

    public void duplicateBlogAddress(String blogAddress) {
        if (blogRepository.existsByBlogAddress(blogAddress)) {
            throw new CustomException(ErrorCode.DUPLICATE_BLOG_ADDRESS);
        }
    }
}
