package com.logus.member.controller;

import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @Autowired
    private MemberRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register/member")
    public Member createUser(@RequestBody Member member) {
        member.encodePassword(passwordEncoder.encode(member.getPassword()));
        return myUserRepository.save(member);
    }

}
