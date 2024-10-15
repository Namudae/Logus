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

<<<<<<< HEAD
import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;
=======
import static java.util.stream.Collectors.toList;
>>>>>>> feature/security

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
        return blogMemberIds.stream()
                .anyMatch(memberId -> memberId.equals(loginId));
    }

    public List<Long> blogMemberIds(Long blogId) {
//        return blogMemberRepository.findMemberIdByBlogId(blogId);
        return blogMemberRepository.findByBlogId(blogId).stream()
                .map(blogMember -> blogMember.getMember().getId())
                .collect(toList());
    }
}
