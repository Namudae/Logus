package com.logus.blog.service;

import com.logus.blog.dto.PostDto;
import com.logus.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostDto.PostResponse> getAllPosts(String blogAddress) {
        return postRepository.getAllPosts(blogAddress);
    }

}
