package com.logus.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContentController {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private MemberDetailService myUserDetailService;

  //without login
  @GetMapping("/home")
  public String handleWelcome() {
    return "Welcome to home!";
  }

//  @PostMapping("/testlogin")
//  public String testlogin() {
//    return """
//           <!DOCTYPE html>
//           <html lang="en">
//           <head>
//               <meta charset="UTF-8">
//               <meta name="viewport" content="width=device-width, initial-scale=1.0">
//               <title>Auto Login</title>
//           </head>
//           <body>
//               <h1>Redirecting to Login...</h1>
//               <script>
//                   // Fetch API를 사용한 POST 요청 (쿠키 포함)
//                   fetch('/login', {
//                       method: 'POST',
//                       headers: {
//                           'Content-Type': 'application/x-www-form-urlencoded'
//                       },
//                       body: new URLSearchParams({
//                           loginId: 'user7',
//                           password: 'user1'
//                       }),
//                       credentials: 'include' // 쿠키를 요청에 포함
//                   })
//                   .then(response => {
//                       if (response.ok) {
//                           // 요청 성공 시 /user/home으로 리다이렉트
//                           window.location.href = "/user/home";
//                       } else {
//                           // 에러 처리 (예: 알림 메시지 출력)
//                           alert('Login failed. Please try again.');
//                       }
//                   })
//                   .catch(error => {
//                       console.error('Error:', error);
//                       alert('An error occurred. Please check the console for details.');
//                   });
//               </script>
//           </body>
//           </html>
//           """;
//  }

  //ADMIN with login
  @GetMapping("/admin/home")
  public String handleAdminHome() {
    return "Welcome to ADMIN home!";
  }

  //USER with login
  @GetMapping("/user/home")
  public String handleUserHome() {
    return "Welcome to USER home!";
  }

  @PostMapping("/authenticate")
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
}
