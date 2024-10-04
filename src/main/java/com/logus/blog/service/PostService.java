package com.logus.blog.service;

import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.*;
import com.logus.blog.repository.*;
import com.logus.common.entity.Attachment;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.repository.AttachmentRepository;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    }

    //+내용 130글자까지?
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable) {
        Page<PostListResponseDto> posts = postRepository.selectAllBlogPosts(blogId, seriesId, pageable);

        List<PostListResponseDto> newPosts = posts.stream()
                .map(dto -> {
                    //imgUrl 처리
                    String imgUrl = dto.getImgUrl();
                    if (imgUrl != null) {
                        dto.setImgUrl(CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl);
                    }

                    // content 앞 130자만 가져오기
                    //html태그 포함인 경우...
//                    String content = dto.getContent();
//                    String textContent = Jsoup.parse(content).text();
//
//                    if (textContent != null && textContent.length() > 130) {
//                        dto.setContent(textContent.substring(0, 130));
//                    }

                    // tags 설정
                    List<String> tags = tagService.selectPostTags(dto.getPostId());
                    dto.setTags(tags);

                    return dto;
                })
                .toList();

        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
//        return postRepository.selectAllBlogPosts(blogAddress, pageable);
    }

    public PostResponseDto selectPost(Long postId) {
        //게시글 조회수+
        Post post = getById(postId);
        PostResponseDto dto = postRepository.selectPost(postId);

        post.addViews(post.getViews()+1);
        postRepository.save(post);
        //댓글 조회
        List<CommentResponseDto> comments = commentService.getComments(postId);
        //태그 조회
        List<String> tags = tagService.selectPostTags(postId);

        //+ 이전게시글, 다음게시글(전체조회 or 시리즈조회)

        dto.setComments(comments);
        dto.setTags(tags);
        return dto;
//        return new PostResponseDto(post, comments, tags);
    }

    public Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Pageable pageable) {
        if(keyword == null) keyword = "";

//        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("postId").descending());
        return postRepository.searchBlogPosts(blogId, keyword, pageable);
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto, MultipartFile thumbImage) throws IOException {

        //1. findById > select로 인한 성능저하
        //2. getReferenceById > 데이터 검증x ***
        Member member = memberService.getById(postRequestDto.getMemberId());
        Blog blog = blogService.getReferenceById(postRequestDto.getBlogId());
        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //임시폴더 이미지 images 폴더로
        List<Attachment> attachments = moveTemporaryImages(postRequestDto);

        //썸네일 업로드
        String thumbUrl = null;
        if (thumbImage != null) {
            thumbUrl = s3Service.thumbUpload(thumbImage);
        }

        Post post = postRequestDto.toEntity(member, blog, category, series, thumbUrl);

        //Post insert
        Post savedPost = postRepository.save(post);

        //Attachment insert
//        for (Attachment attachment : attachments) {
//            attachment.setPost(savedPost);
//            attachmentRepository.save(attachment);
//        }

        //Tag insert
        tagService.savePostTag(postRequestDto, savedPost);

        return savedPost.getId();
    }

    @Transactional
    public Long updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = getById(postId);

        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //삭제된 이미지 처리
        deleteImages(post.getContent(), postRequestDto.getContent());

        //임시폴더 이미지 images 폴더로
        List<Attachment> attachments = moveTemporaryImages(postRequestDto);

        post.updatePost(category, series, postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getStatus());

        //Attachment 생략

        //태그 처리 추가
        // post_tag 삭제하고 새로 insert
        tagService.deleteOldPostTag(postId);
        postRepository.flush();
        tagService.savePostTag(postRequestDto, post);

        return postId;
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getById(postId);
        post.deletePost();
        postRepository.save(post);
    }

    private List<Attachment> moveTemporaryImages(PostRequestDto postRequestDto) {
        List<Attachment> attachments = new ArrayList<>();
        Document document = Jsoup.parse(postRequestDto.getContent());
        String content = postRequestDto.getContent();
        Elements imageElements = document.getElementsByTag("img");

        if (imageElements.size() > 0) {
            for (Element imageElement : imageElements) {
                String source = imageElement.attr("src");

                if (!source.contains(CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.TEMP.getPath() + "/")) {
                    continue;
                }

                String oldSource = source.replace(CLOUD_FRONT_DOMAIN_NAME + "/", "");
                String newSource = oldSource.replace(AttachmentType.TEMP.getPath(), AttachmentType.IMAGE.getPath());

                s3Service.update(oldSource, newSource);

                Attachment attachment = Attachment.builder()
                        .filepath(newSource)
                        .attachmentType(AttachmentType.IMAGE)
                        .filename(newSource.split("/")[1])
                        .build();
                attachments.add(attachment);
            }
        }
        content = content.replace(CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.TEMP.getPath(), CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.IMAGE.getPath());

        postRequestDto.setContent(content);
        return attachments;
    }

    private void deleteImages(String oldContent, String newContent) {
        //oldContent 사진 돌면서 newContent에 포함되는지 비교, 없으면 서버에서 삭제
        List<String> oldImageList = extractImageSrcList(oldContent);
        List<String> newImageList = extractImageSrcList(newContent);

        //수정 전 게시글 이미지 중, 수정후에 없는 이미지만 삭제
        for (String oldImage : oldImageList) {
            if (!newImageList.contains(oldImage)) {
                s3Service.deleteS3(oldImage);
            }
        }
    }

    private List<String> extractImageSrcList(String content) {
        List<String> imageList = new ArrayList<>();
        Document document = Jsoup.parse(content);
        Elements imageElements = document.getElementsByTag("img");

        for (Element imageElement : imageElements) {
            String src = imageElement.attr("src");

            if (!src.contains(CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.IMAGE.getPath() + "/")) {
                continue;
            }

            // "/images/파일명.확장자" 부분만 추출
            String imagePath = src.substring(src.indexOf(AttachmentType.IMAGE.getPath() + "/"));
            imageList.add(imagePath);
        }
        return imageList;
    }

}
