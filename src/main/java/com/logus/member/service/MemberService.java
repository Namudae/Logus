package com.logus.member.service;

import com.logus.blog.entity.Blog;
import com.logus.blog.service.BlogService;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.JwtService;
import com.logus.common.security.LoginForm;
import com.logus.common.security.MemberDetailService;
import com.logus.common.service.S3Service;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.dto.MemberResponse;
import com.logus.member.dto.RegisterRequest;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@Service
@RequiredArgsConstructor
public class MemberService {

//    @Autowired
    private final JwtService jwtService;
    private final BlogService blogService;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final MemberDetailService memberDetailService;

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

    public MemberResponse login(LoginForm loginForm) {
        Member member = memberRepository.findByLoginId(loginForm.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.loginId(), loginForm.password()
        ));
        if (authentication.isAuthenticated()) {
            return MemberResponse.builder()
                    .memberId(member.getId())
                    .loginId(member.getLoginId())
                    .nickname(member.getNickname())
                    .imgUrl(
                            (member.getImgUrl()!=null ? CLOUD_FRONT_DOMAIN_NAME + "/" + member.getImgUrl() : null)
                    )
                    .email(member.getEmail())
                    .jwtToken(jwtService.generateToken(memberDetailService.loadUserByUsername(loginForm.loginId())))
                    .build();
//            response.setJwtToken(jwtService.generateToken(memberDetailService.loadUserByUsername(loginForm.loginId())));
        } else {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
    }

    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        return memberRepository.searchMembers(loginId, nickname, blogName, blogAddress, pageable);
    }
}
