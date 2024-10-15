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
import com.logus.common.security.JwtService;
import com.logus.common.service.S3Service;
import com.logus.member.entity.Member;
import com.logus.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final BlogService blogService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final SeriesService seriesService;
    private final TagService tagService;
    private final CommentService commentService;
    private final S3Service s3Service;

    @Autowired
    private JwtService jwtService;

    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    //+내용 130글자까지?
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable, HttpServletRequest request) {
        //본인 확인
        Long requestId = memberService.getMemberIdFromJwt(request);

        Page<PostListResponseDto> posts = postRepository.selectAllBlogPosts(blogId, seriesId, pageable, requestId);
        List<PostListResponseDto> newPosts = toPostList(posts);

        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
//        return postRepository.selectAllBlogPosts(blogAddress, pageable);
    }

    public PostResponseDto selectPost(Long postId, HttpServletRequest request) {
        //본인 확인
        Long requestId = memberService.getMemberIdFromJwt(request);
        Post post = getById(postId);

        //작성자가 아닐 경우
//        if (!memberService.isAuthor(requestId, post.getMember().getId())) {
//            if(post.getStatus() != Status.PUBLIC) {
//                throw new CustomException(ErrorCode.SECRET_POST);
//            }
//        }

        //비밀글 > 멤버확인, 임시글 > 조회x
        if (post.getStatus() == Status.SECRET) {
            List<Long> blogMemberIds = blogService.blogMemberIds(post.getBlog().getId());
            if (!blogService.isMember(requestId, blogMemberIds)) {
                throw new CustomException(ErrorCode.SECRET_POST);
            }
        } else if (post.getStatus() == Status.TEMPORARY) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        //게시글 조회수+
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
    }

    public Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Pageable pageable) {
        if(keyword == null) keyword = "";
        return postRepository.searchBlogPosts(blogId, keyword, pageable);
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto, MultipartFile thumbImage) throws IOException {

        //1. findById > select 쿼리 나감
        //2. getReferenceById > 데이터 검증x
        Member member = memberService.getById(postRequestDto.getMemberId());
        Blog blog = blogService.getReferenceById(postRequestDto.getBlogId());
        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //임시폴더 이미지 images 폴더로
        List<Attachment> attachments = moveTemporaryImages(postRequestDto);

        //썸네일 업로드
        String thumbUrl = null;
        if (thumbImage != null || !thumbImage.isEmpty()) {
            thumbUrl = s3Service.thumbUpload(thumbImage);
        }

        Post post = postRequestDto.toEntity(member, blog, category, series, thumbUrl);

        //Post insert
        Post savedPost = postRepository.save(post);

        //Attachment insert(생략)
//        for (Attachment attachment : attachments) {
//            attachment.setPost(savedPost);
//            attachmentRepository.save(attachment);
//        }

        //Tag insert
        tagService.savePostTag(postRequestDto, savedPost);

        return savedPost.getId();
    }

    @Transactional
    public Long updatePost(Long postId, PostRequestDto postRequestDto, MultipartFile thumbImage, boolean deleteThumb) throws IOException {
        Post post = getById(postId);

        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        //썸네일 처리
        if (deleteThumb) {
            //기존 썸네일 처리(s3 삭제, ImgUrl 지우기)
            s3Service.deleteS3(post.getImgUrl());
            post.deleteImgUrl();
        }
        //thumbImage 빈값아니면 썸네일 업로드
        if (thumbImage != null && !thumbImage.isEmpty()) {
            String thumbUrl = s3Service.thumbUpload(thumbImage);
            post.changeImgUrl(thumbUrl);
        }

        //삭제된 이미지 처리
        deleteImages(post.getContent(), postRequestDto.getContent());

        //임시폴더 이미지 images 폴더로
        moveTemporaryImages(postRequestDto);

        post.updatePost(category, series, postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getStatus());

        //Attachment 생략

        //태그 처리 추가
        // post_tag 삭제하고 새로 insert
        tagService.deletePostTag(postId);
        postRepository.flush();
        tagService.savePostTag(postRequestDto, post);

        return postId;
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getById(postId);

        //댓글 delete
        commentService.getByPostId(postId).forEach(commentService::delete);

        //postTag delete
        tagService.deletePostTag(postId);

        //썸네일 서버 삭제
        if (post.getImgUrl() != null && !post.getImgUrl().isEmpty()) {
            s3Service.deleteS3(post.getImgUrl());
        }
        //이미지 서버 삭제
        List<String> imageList = extractImageSrcList(post.getContent());
        for (String image : imageList) {
            s3Service.deleteS3(image);
        }
    }

    public Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, String tag, Pageable pageable) {
        //태그명으로 tagId > post_tag에서 검색 > 반환
        Long tagId = tagService.findByTagName(tag).getId();

        //blogId, tagId로 조건걸어서 반환
        Page<PostListResponseDto> posts = postRepository.searchBlogPostsByTag(blogId, tagId, pageable);
        List<PostListResponseDto> newPosts = toPostList(posts);
        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
    }


    /**
     * 게시글 목록 조회 공통처리
     * - 썸네일
     * - 태그
     * 추가할것: 본문 n자까지만 조회
     */
    private List<PostListResponseDto> toPostList(Page<PostListResponseDto> posts) {
        return posts.stream()
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
