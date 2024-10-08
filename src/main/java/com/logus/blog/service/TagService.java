package com.logus.blog.service;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.PostTag;
import com.logus.blog.entity.Tag;
import com.logus.blog.repository.PostTagRepository;
import com.logus.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public void savePostTag(PostRequestDto postRequestDto, Post savedPost) {
        if (postRequestDto.getTags() != null) {
            //1. tag insert
            List<Tag> tags = insertTags(postRequestDto.getTags());
            //2. postTag insert
            insertPostTags(savedPost, tags);
        }
    }

    public List<Tag> insertTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag existTag = tagRepository.findByTagName(tagName);
            if (existTag == null) {
                Tag newTag = Tag.builder()
                        .tagName(tagName)
                        .build();
                tags.add(tagRepository.save(newTag));
            } else {
                tags.add(existTag);
            }
        }

        return tags;
    }

    public void insertPostTags(Post post, List<Tag> tags) {
        for (Tag tag : tags) {
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
        }
    }

    public void deletePostTag(Long postId) {
        postTagRepository.deleteAllByPostId(postId);
    }

    public List<String> selectPostTags(Long postId) {
        return postTagRepository.selectPostTag(postId);
    }

    public Tag findByTagName(String tagname) {
        return tagRepository.findByTagName(tagname);
    }

}
