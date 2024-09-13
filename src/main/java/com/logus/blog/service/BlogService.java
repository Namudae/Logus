package com.logus.blog.service;

import com.logus.blog.dto.BlogMemberRequestDto;
import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogMember;
import com.logus.blog.repository.BlogMemberRepository;
import com.logus.blog.repository.BlogRepository;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final MemberService memberService;

    public Blog getById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("BLOG NOT FOUND"));
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

}
