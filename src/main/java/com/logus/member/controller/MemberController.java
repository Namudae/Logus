package com.logus.member.controller;

import com.logus.common.controller.ApiResponse;
import com.logus.common.security.JwtService;
import com.logus.common.security.LoginForm;
import com.logus.common.security.MemberDetailService;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.dto.RegisterRequest;
import com.logus.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MemberDetailService myUserDetailService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.loginId(), loginForm.password()
        ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(myUserDetailService.loadUserByUsername(loginForm.loginId()));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Long>> createUser(@RequestPart("requestDto") @Valid RegisterRequest registerRequest,
                                                     @RequestPart(value = "memberImg", required = false) MultipartFile memberImg,
                                                     @RequestPart(value = "blogImg", required = false) MultipartFile blogImg) {
        Long blogId = memberService.createMember(registerRequest, memberImg, blogImg);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 아이디 중복 확인
     */
    @GetMapping("/register/dupl")
    public ApiResponse<String> duplicateLoginId(@RequestParam String loginId) {
        memberService.duplicateLoginId(loginId);
        return ApiResponse.ok();
    }

    /**
     * 회원 정보 검색
     * - 아이디, 닉네임, 블로그명, 블로그 주소
     */
    @GetMapping("/user")
    public ApiResponse<Page<MemberListResponse>> searchMembers(@RequestParam(value = "loginId", required = false) String loginId,
                                                               @RequestParam(value = "nickname", required = false) String nickname,
                                                               @RequestParam(value = "blogName", required = false) String blogName,
                                                               @RequestParam(value = "blogAddress", required = false) String blogAddress,
                                                               Pageable pageable) {
        Page<MemberListResponse> memberLists = memberService.searchMembers(loginId, nickname, blogName, blogAddress, pageable);

        return ApiResponse.ok(memberLists);
    }

}
