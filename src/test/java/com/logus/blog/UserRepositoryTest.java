package com.logus.blog;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testUser() throws Exception {
        //given
        User user = new User();
        user.setNickname("user1");

        //when
        Long savedId = userRepository.save(user);
        User findUser = userRepository.find(savedId);

        //then
        Assertions.assertThat(findUser.getId()==(user.getId()));
        Assertions.assertThat(findUser.getNickname()==(user.getNickname()));
        Assertions.assertThat(findUser).isEqualTo(user); //true(같은 영속성 컨텍스트에서 관리하기 때문에)
    }
}