package com.logus.blog.repository;

import com.logus.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    //메서드 자동생성
//    List<Post> findById(Long id);
}
