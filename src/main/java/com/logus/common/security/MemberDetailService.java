package com.logus.common.security;

import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailService implements UserDetailsService {

    @Autowired
    private MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         Optional<Member> user = repository.findByLoginId(username);
        if (user.isPresent()) {
            var userObj = user.get();
//            return User.builder()
//                    .username(userObj.getLoginId())
//                    .password(userObj.getPassword())
//                    .roles(getRoles(userObj))
//                    .build();
            return new UserPrincipal(userObj);
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String[] getRoles(Member user) {
        if (user.getRole() == null) {
            return new String[]{"ROLE_USER"};
        }
        return user.getRole().split(",");
    }
}
