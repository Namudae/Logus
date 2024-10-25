package com.logus.blog.repository;

import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    //메서드 자동생성
    List<Post> findByBlogId(Long blogId);
}
