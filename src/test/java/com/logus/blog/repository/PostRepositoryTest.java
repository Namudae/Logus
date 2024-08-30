package com.logus.blog.repository;

import com.logus.blog.entity.Post;
import com.logus.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    @Test
    public void saveTest() {
        Member member = new Member("user2");
        memberRepository.save(member);

        Post post = new Post("테스트 제목입니다.", "테스트 내용입니다.", member);
    }

}