package com.logus.member.service;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.Series;
import com.logus.blog.service.BlogService;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.JwtService;
import com.logus.common.security.LoginForm;
import com.logus.common.security.MemberDetailService;
import com.logus.common.security.UserPrincipal;
import com.logus.common.service.S3Service;
import com.logus.member.dto.*;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Transactional
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

    @Transactional
    public MemberResponse login(LoginForm loginForm, HttpServletResponse response) {
        Member member = memberRepository.findByLoginId(loginForm.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginForm.loginId(), loginForm.password()
            ));
            if (authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(memberDetailService.loadUserByUsername(loginForm.loginId()));

                // JWT 토큰을 쿠키에 저장
                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setMaxAge(60 * 60 * 24 * 90);  // 7일 동안 유효
                cookie.setPath("/");  // 모든 경로에서 유효
                cookie.setHttpOnly(true);  // JavaScript에서 접근 불가
//                cookie.setSecure(true);  // HTTPS에서만 전송
                response.addCookie(cookie);  // 쿠키를 응답에 추가

                return MemberResponse.builder()
                        .memberId(member.getId())
                        .loginId(member.getLoginId())
                        .nickname(member.getNickname())
                        .imgUrl(
                                (member.getImgUrl() != null ? CLOUD_FRONT_DOMAIN_NAME + "/" + member.getImgUrl() : null)
                        )
                        .email(member.getEmail())
                        .jwtToken(jwtToken)
                        .build();
            } else {
                throw new CustomException(ErrorCode.LOGIN_FAIL);
            }
        } catch (BadCredentialsException e) {
            // 비밀번호 틀린 경우
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. JWT 토큰을 삭제하기 위해 쿠키를 찾아서 만료시킴
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    // JWT 쿠키 삭제
                    cookie.setValue(null);
                    cookie.setMaxAge(0);  // 쿠키를 즉시 만료시키기
                    cookie.setPath("/");  // 모든 경로에서 유효
                    cookie.setHttpOnly(true);  // JavaScript에서 접근 불가
//                    cookie.setSecure(true);  // HTTPS에서만 전송
//                    cookie.setSameSite("Strict");  // CSRF 방지
                    response.addCookie(cookie);  // 쿠키를 응답에 추가하여 삭제 처리
                }
            }
        }
    }

    @Transactional
    public MemberResponse selectUserInfo() {
        Long memberId = authMemberId();
        Member member = getById(memberId);

        return MemberResponse.builder()
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .imgUrl(
                        (member.getImgUrl() != null ? CLOUD_FRONT_DOMAIN_NAME + "/" + member.getImgUrl() : null)
                )
                .email(member.getEmail())
                .build();
    }

    @Transactional
    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        return memberRepository.searchMembers(loginId, nickname, blogName, blogAddress, pageable);
    }

    @Transactional
    public Long updateMember(UserInfoRequest userInfo, MultipartFile img, boolean deleteImg) throws IOException {
        Long memberId = authMemberId();
        Member member = getById(memberId);

        // 현재 비밀번호 검증
        if (userInfo.getNewPassword() != null) {
            if (userInfo.getPassword()==null) {
                throw new CustomException(ErrorCode.PASSWORD_FAIL);
            }
            if (!passwordEncoder.matches(userInfo.getPassword(), member.getPassword())) {
                throw new CustomException(ErrorCode.PASSWORD_FAIL);
            }
            userInfo.setNewPassword(passwordEncoder.encode(userInfo.getNewPassword()));
        }

        //이미지 처리
        if (deleteImg) {
            //기존 썸네일 처리(s3 삭제, ImgUrl 지우기)
            s3Service.deleteS3(member.getImgUrl());
            member.deleteImgUrl();
        }
        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.imgUpload(img, AttachmentType.PROFILE);
        }
        member.updateMemberInfo(userInfo, imgUrl);

        return memberId;
    }

    public boolean checkPassword(PasswordDto passwordDto) {
        Long memberId = authMemberId();
        Member member = getById(memberId);
        String password = passwordDto.getPassword();

        if (password==null || !passwordEncoder.matches(password, member.getPassword())) {
            return false;
        } else {
            return true;
        }

    }


    //==========인가==========
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

    public boolean hasPermissionToMember(Long targetId, String targetType) {
        var member = memberRepository.findById((Long) targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (!Objects.equals(authMemberId(), targetId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
        return true;
    }

    public MemberSearchResponse searchMemberByEmail(String email) {
        // Optional을 사용하여 Member 조회
        Member me = getById(authMemberId());
        if (me.getEmail().equals(email)) {
            return null;
        }
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        // 검색 결과가 없으면 null 반환
        if (memberOptional.isEmpty()) {
            return null;
        }
        Member member = memberOptional.get();

        String imgUrl = (member.getImgUrl() != null && !member.getImgUrl().isEmpty())
                ? CLOUD_FRONT_DOMAIN_NAME + "/" + member.getImgUrl()
                : null;
        return MemberSearchResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imgUrl(imgUrl)
                .build();
    }
}
