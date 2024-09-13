package com.logus.blog.service;

import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.*;
import com.logus.blog.exception.PostNotFound;
import com.logus.blog.repository.*;
import com.logus.common.entity.Attachment;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.repository.AttachmentRepository;
import com.logus.common.service.AttachmentService;
import com.logus.common.service.S3Service;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final AttachmentRepository attachmentRepository;
    private final BlogService blogService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final SeriesService seriesService;
    private final TagService tagService;
    private final CommentService commentService;
    private final S3Service s3Service;

    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
//                .orElseThrow(PostNotFound::new);
    }

    //+내용 130글자까지?
    public Page<PostResponseDto> selectAllBlogPosts(String blogAddress, Pageable pageable) {
        return postRepository.selectAllBlogPosts(blogAddress, pageable);
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto) {

        //1. findById > select로 인한 성능저하
        //2. getReferenceById > 데이터 검증x ***
        Member member = memberService.getById(postRequestDto.getMemberId());
        Blog blog = blogService.getReferenceById(postRequestDto.getBlogId());
        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //임시폴더 이미지 images 폴더로
        List<Attachment> attachments = moveTemporaryImages(postRequestDto);

        Post post = postRequestDto.toEntity(member, blog, category, series);

        //Post insert
        Post savedPost = postRepository.save(post);

        //Attachment insert
        for (Attachment attachment : attachments) {
            attachment.setPost(savedPost);
            attachmentRepository.save(attachment);
        }

        //Tag insert
        if (postRequestDto.getTags() != null) {
            //1. tag insert
            List<Tag> tags = tagService.insertTags(postRequestDto.getTags());
            //2. postTag insert
            tagService.insertPostTags(savedPost, tags);
        }

        return savedPost.getId();
    }

    private List<Attachment> moveTemporaryImages(PostRequestDto postRequestDto) {
        List<Attachment> attachments = new ArrayList<>();
        Document document = Jsoup.parse(postRequestDto.getContent());
        String content = postRequestDto.getContent();
        Elements imageElements = document.getElementsByTag("img");

        if (imageElements.size() > 0) {
            for (Element imageElement : imageElements) {
                String source = imageElement.attr("src");

                if (!source.contains("/temporary/")) {
                    continue;
                }

                String oldSource = source.replace("https://" + CLOUD_FRONT_DOMAIN_NAME + "/", "");
                String newSource = oldSource.replace("temporary", "images");

                s3Service.update(oldSource, newSource);

                Attachment attachment = Attachment.builder()
                        .filepath(newSource)
                        .attachmentType(AttachmentType.IMAGE)
                        .filename(newSource.split("/")[1])
                        .build();
                attachments.add(attachment);
            }
        }
        content = content.replace("https://" + CLOUD_FRONT_DOMAIN_NAME + "/temporary", "https://" + CLOUD_FRONT_DOMAIN_NAME + "/images");

        postRequestDto.setContent(content);
        return attachments;
    }


    public PostResponseDto selectPost(String blogAddress, Long postId) {
        //게시글 조회
        Post post = getById(postId);
        post.setViews(post.getViews()+1);
        postRepository.save(post);
        //댓글 조회
        List<CommentResponseDto> comments = commentService.getComments(postId);

        return new PostResponseDto(post, comments);
    }

    public Page<PostResponseDto> searchBlogPosts(String blogAddress, String keyword, Pageable pageable) {
        if(keyword == null) keyword = "";

//        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("postId").descending());
        return postRepository.searchBlogPosts(blogAddress, keyword, pageable);
    }
}
