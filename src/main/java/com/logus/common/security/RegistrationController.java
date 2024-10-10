package com.logus.common.security;

import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private MemberRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register/user")
    public Member createUser(@RequestBody Member user) {
        user.encodePassword(passwordEncoder.encode(user.getPassword()));
        return myUserRepository.save(user);
    }

}
