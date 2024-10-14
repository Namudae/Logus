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
