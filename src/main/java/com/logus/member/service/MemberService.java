package com.logus.member.service;

import com.logus.blog.entity.Blog;
import com.logus.blog.service.BlogService;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.JwtService;
import com.logus.common.service.S3Service;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.dto.RegisterRequest;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

//    @Autowired
    private final JwtService jwtService;
    private final BlogService blogService;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    public Member getReferenceById(Long memberId) {
        return memberId == null ? null :
                memberRepository.getReferenceById(memberId);
    }

    public Long findIdByLoginId(String loginId) {
        if (loginId == null) {
            return null;
        }
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getId();
    }

    public Long getMemberIdFromJwt(HttpServletRequest request) {
        return findIdByLoginId(jwtService.extractUsername(jwtService.getJwt(request)));
    }

    public boolean isAuthor(Long loginId, Long authorId) {
        return loginId.equals(authorId);
    }

    public Long createMember(RegisterRequest registerRequest, MultipartFile memberImg) throws IOException {

        //멤버 중복체크
        duplicateLoginId(registerRequest.getLoginId());
        //블로그 중복체크
        blogService.duplicateBlogAddress(registerRequest.getBlogRequestDto().getBlogAddress());

        //프로필 이미지
        String imgUrl = null;
        if (memberImg != null && !memberImg.isEmpty()) {
            imgUrl = s3Service.imgUpload(memberImg, AttachmentType.PROFILE);
        }

        Member member = registerRequest.toEntity(imgUrl);
        member.encodePassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);

        Blog blog = blogService.registerBlog(member, registerRequest.getBlogRequestDto());

        return blog.getId();
    }

    public void duplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        //keyword: 아이디, 닉네임, 블로그명, 블로그 주소
        return memberRepository.searchMembers(loginId, nickname, blogName, blogAddress, pageable);
    }

}
