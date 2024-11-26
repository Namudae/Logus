package com.logus.member.controller;

import com.logus.common.controller.ApiResponse;
import com.logus.common.security.JwtService;
import com.logus.common.security.LoginForm;
import com.logus.common.security.MemberDetailService;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.dto.MemberResponse;
import com.logus.member.dto.RegisterRequest;
import com.logus.member.dto.UserInfoRequest;
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

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 로그인
     */
    @PostMapping("/login")
//    @RequestMapping(value = {"/login"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ApiResponse<MemberResponse> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        MemberResponse response = memberService.login(loginForm);
        return ApiResponse.ok(response);
    }
    /**
     * 나의 회원정보 조회
     */
    @GetMapping("/user")
    public ApiResponse<MemberResponse> selectUserInfo() throws IOException {
        MemberResponse response = memberService.selectUserInfo();
        return ApiResponse.ok(response);
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Long>> createUser(@RequestPart("requestDto") @Valid RegisterRequest registerRequest,
                                                     @RequestPart(value = "memberImg", required = false) MultipartFile memberImg) throws IOException {
        Long blogId = memberService.createMember(registerRequest, memberImg);
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
     * 회원정보 변경
     */
    @PutMapping("/user")
    public ApiResponse<Map<String, Long>> updateUser(@RequestBody @Valid UserInfoRequest userInfo) throws IOException {
        Long memberId = memberService.updateMember(userInfo);
        return ApiResponse.ok(Map.of("memberId", memberId));
    }

    /**
     * 탈퇴
     * - 내가 OWNER인 블로그 삭제(+ 팔로우)
     * - 내가 포함된 blogMember 삭제
     * - 내가 작성자인 글 삭제
     * - 내가 작성자인 댓글 삭제
     * - 좋아요 삭제
     */

}
