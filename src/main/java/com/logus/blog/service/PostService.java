package com.logus.blog.service;

import com.logus.admin.dto.AdminCommentListResponse;
import com.logus.admin.dto.AdminPostListResponse;
import com.logus.admin.entity.Category;
import com.logus.admin.entity.ReportStatus;
import com.logus.admin.service.CategoryService;
import com.logus.blog.dto.*;
import com.logus.blog.entity.*;
import com.logus.blog.repository.*;
import com.logus.common.config.CustomHtmlEscapeUtil;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.JwtService;
import com.logus.common.security.UserPrincipal;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private final LikeyRepository likeyRepository;

    @Autowired
    private JwtService jwtService;

    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    //+내용 130글자까지?
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable) {
        //본인 확인
        Long requestId = blogService.authMemberIdOrNull();

        Page<PostListResponseDto> posts = postRepository.selectAllBlogPosts(blogId, seriesId, pageable, requestId);
        List<PostListResponseDto> newPosts = toPostList(posts);

        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
//        return postRepository.selectAllBlogPosts(blogAddress, pageable);
    }

    public PostResponseDto selectPost(Long postId) {
        //본인 확인
        Long memberId = blogService.authMemberIdOrNull();
        Post post = getById(postId);

        //비밀글 > 멤버확인, 임시글 > 조회x
        List<Long> blogMemberIds = blogService.blogMemberIds(post.getBlog().getId());
        boolean isMember = blogService.isMember(memberId, blogMemberIds);
        if (post.getStatus() == Status.SECRET) {
            if (!isMember) {
                throw new CustomException(ErrorCode.SECRET_POST);
            }
        } else if (post.getStatus() == Status.TEMPORARY) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        //게시글 조회수+
        PostResponseDto dto = postRepository.selectPost(postId);
        post.addViews(post.getViews()+1);
        postRepository.save(post);

        //이스케이프
//        dto.setContent(CustomHtmlEscapeUtil.escapeCustom(dto.getContent()));

        //좋아요 조회
        if (likeyRepository.findByMemberIdAndPostId(memberId, postId).isPresent()) {
            dto.setLiked(true);
        } else {
            dto.setLiked(false);
        }

        //댓글 조회
//        List<ParentCommentDto> comments = commentService.getComments(postId);
//        // + 전체 조회후 비밀댓글 처리
//        if (!isMember) {
//            comments.stream()
//                    .filter(comment -> comment.getStatus() == Status.SECRET)
//                    .forEach(ParentCommentDto::secretComment);
//        }
        CommentResponseDto comments = commentService.getParentChildComments(postId, isMember);
        dto.setComments(comments);

        //썸네일
        String imgUrl = dto.getImgUrl();
        if (imgUrl != null && !imgUrl.equals("")) {
            dto.setImgUrl(CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl);
        }

        //태그 조회
        List<String> tags = tagService.selectPostTags(postId);
        //이전게시글, 다음게시글(전체조회 기준, PUBLIC)
        PostResponseDto pre = postRepository.selectPrePost(post);
        PostResponseDto next = postRepository.selectNextPost(post);
        dto.setPreNext(pre, next);

        dto.setComments(comments);
        dto.setTags(tags);

        //블라인드
        if (post.getReportStatus() == ReportStatus.BLIND) {
            dto.blindPost();
        } else if (post.getReportStatus() == ReportStatus.BLOCK) {
            dto.blockPost();
        }

        return dto;
    }

    @Transactional
    public Long createPost(PostRequestDto postRequestDto, MultipartFile thumbImage) throws IOException {
        // memberId
        Long memberId = blogService.authMemberId();

        Member member = memberService.getReferenceById(memberId);
        Blog blog = blogService.getReferenceById(postRequestDto.getBlogId());
        Category category = categoryService.getReferenceById(postRequestDto.getCategoryId());
        Series series = seriesService.getReferenceById(postRequestDto.getSeriesId());

        moveTemporaryImages(postRequestDto);

        //썸네일 업로드
        String thumbUrl = null;
        if (thumbImage != null && !thumbImage.isEmpty()) {
            thumbUrl = s3Service.imgUpload(thumbImage, AttachmentType.THUMB);
        }

        //이스케이프
//        postRequestDto.setContent(CustomHtmlEscapeUtil.escapeCustom(postRequestDto.getContent()));
//        postRequestDto.setTitle(CustomHtmlEscapeUtil.escapeCustom(postRequestDto.getTitle()));

        //임시저장글일 경우, 기존 임시저장글 삭제, 새로 insert
        if (postRequestDto.getStatus() == Status.TEMPORARY) {
            // temporary인 post 찾아서 update
            Optional<TempPostResponseDto> tempPostOptional = postRepository.selectTemp(postRequestDto.getBlogId(), memberId);
            tempPostOptional.ifPresent(tempPost -> {
                Long tempPostId = tempPost.getPostId();
                deletePost(tempPostId);
            });
        }
        Post post = postRequestDto.toEntity(member, blog, category, series, thumbUrl);
        //Post insert
        Post savedPost = postRepository.save(post);

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
            String thumbUrl = s3Service.imgUpload(thumbImage, AttachmentType.THUMB);
            post.changeImgUrl(thumbUrl);
        }

        //삭제된 이미지 처리
        deleteOldImages(post.getContent(), postRequestDto.getContent());

        //임시폴더 이미지 images 폴더로
        moveTemporaryImages(postRequestDto);

        //이스케이프
//        postRequestDto.setContent(CustomHtmlEscapeUtil.escapeCustom(postRequestDto.getContent()));
//        postRequestDto.setTitle(CustomHtmlEscapeUtil.escapeCustom(postRequestDto.getTitle()));

        // 기존 임시저장 & 새글 발행 > 생성날짜 update
        if (post.getStatus() == Status.TEMPORARY && postRequestDto.getStatus() == Status.PUBLIC) {
            post.changeTempDate(LocalDateTime.now());
        }
        post.updatePost(category, series, postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getStatus());

        //태그 처리 추가 (post_tag 삭제하고 새로 insert)
        tagService.deletePostTag(postId);
        postRepository.flush();
        tagService.savePostTag(postRequestDto, post);

        return postId;
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getById(postId);

        //댓글
        commentService.bulkDeleteComment(postId);
        //좋아요
        likeyRepository.bulkDeleteByPostId(postId);
        //postTag
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

        postRepository.delete(post);
    }

    public TempPostResponseDto selectTempPost(Long blogId) {
        // selectTemp()가 Optional을 반환하도록 수정
        Optional<TempPostResponseDto> tempOptional = postRepository.selectTemp(blogId, blogService.authMemberId());

        // Optional이 비어있다면 null 반환
        if (tempOptional.isEmpty()) {
            return null;
        }

        // Optional에서 값 추출
        TempPostResponseDto temp = tempOptional.get();

        //태그 조회
        List<String> tags = tagService.selectPostTags(temp.getPostId());
        temp.setTags(tags);
        return temp;
    }

    public Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, String condition, Pageable pageable) {
        // memberId
        Long memberId = blogService.authMemberIdOrNull();
        //Auth 추출
//        Blog blog = blogService.getById(blogId);
//        boolean isMember = blogService.isBlogMember(blog, memberId);

        if(keyword == null) keyword = "";
        Page<PostListResponseDto> posts =  postRepository.searchBlogPosts(blogId, keyword, condition, memberId, pageable);
        List<PostListResponseDto> newPosts = toPostList(posts);
        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
    }

    public Page<PostListResponseDto> searchBlogPostsByMember(Long blogId, String keyword, String condition, Pageable pageable) {
        // memberId
        Long memberId = blogService.authMemberIdOrNull();

        if(keyword == null) keyword = "";
        Page<PostListResponseDto> posts =  postRepository.searchBlogPostsByMember(blogId, keyword, condition, memberId, pageable);
        List<PostListResponseDto> newPosts = toPostListNotTag(posts);
        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
    }

    public Page<AdminPostListResponse> searchPostsByAdmin(String keyword, String condition, Pageable pageable) {
        if(keyword == null) keyword = "";
        return postRepository.searchPostsByAdmin(keyword, condition, pageable);

    }

    public Page<AdminCommentListResponse> searchCommentsByAdmin(String keyword, String condition, Pageable pageable) {
        if(keyword == null) keyword = "";
        return postRepository.searchCommentsByAdmin(keyword, condition, pageable);
    }

    public Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, String tag, Pageable pageable) {
        // memberId
        Long memberId = blogService.authMemberIdOrNull();

        //태그명으로 tagId > post_tag에서 검색 > 반환
        Long tagId = tagService.findByTagName(tag).getId();

        //blogId, tagId로 조건걸어서 반환
        Page<PostListResponseDto> posts = postRepository.searchBlogPostsByTag(blogId, tagId, memberId, pageable);
        List<PostListResponseDto> newPosts = toPostList(posts);
        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
    }


    /**
     * 게시글 목록 조회 공통처리
     * - 썸네일
     * - 태그
     * - 블라인드
     */
    private List<PostListResponseDto> toPostList(Page<PostListResponseDto> posts) {
        return posts.stream()
                .map(dto -> {
                    //imgUrl 처리
                    String imgUrl = dto.getImgUrl();
                    if (imgUrl != null && !imgUrl.equals("")) {
                        dto.setImgUrl(CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl);
                    }
                    // tags 설정
                    List<String> tags = tagService.selectPostTags(dto.getPostId());
                    dto.setTags(tags);
                    //블라인드 처리
                    if (Objects.equals(dto.getReportStatus(), ReportStatus.BLIND)) {
                        dto.blindPost();
                    } else if (Objects.equals(dto.getReportStatus(), ReportStatus.BLOCK)) {
                        dto.blockPost();
                    }

                    return dto;
                })
                .toList();
    }

    private List<PostListResponseDto> toPostListNotTag(Page<PostListResponseDto> posts) {
        return posts.stream()
                .map(dto -> {
                    //imgUrl 처리
                    String imgUrl = dto.getImgUrl();
                    if (imgUrl != null && !imgUrl.equals("")) {
                        dto.setImgUrl(CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl);
                    }
                    //블라인드 처리
                    if (Objects.equals(dto.getReportStatus(), ReportStatus.BLIND)) {
                        dto.blindPost();
                    } else if (Objects.equals(dto.getReportStatus(), ReportStatus.BLOCK)) {
                        dto.blockPost();
                    }
                    return dto;
                })
                .toList();
    }

    // ======== 이미지 처리 ========
    private void moveTemporaryImages(PostRequestDto postRequestDto) {

        //이스케이프 해제
//        String unescapedContent = CustomHtmlEscapeUtil.unescapeCustom(postRequestDto.getContent());
        String content = postRequestDto.getContent();

//        List<Attachment> attachments = new ArrayList<>();
        Document document = Jsoup.parse(content);
//        String content = content;
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

//                Attachment attachment = Attachment.builder()
//                        .filepath(newSource)
//                        .attachmentType(AttachmentType.IMAGE)
//                        .filename(newSource.split("/")[1])
//                        .build();
//                attachments.add(attachment);
            }
        }
        content = content.replace(CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.TEMP.getPath(), CLOUD_FRONT_DOMAIN_NAME + "/" + AttachmentType.IMAGE.getPath());

        postRequestDto.setContent(content);
    }

    private void deleteOldImages(String oldContent, String newContent) {
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
        //이스케이프 해제
        String unescapedContent = CustomHtmlEscapeUtil.unescapeCustom(content);
        List<String> imageList = new ArrayList<>();
        Document document = Jsoup.parse(unescapedContent);
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

    // ======== 인가 ========
    public boolean hasPermissionToPost(Long postId, Authentication authentication) {
        // 로그인이 안 되어 있거나, 익명 사용자인 경우 예외 발생
        Long memberId = memberService.authMemberId();
        var post = postRepository.findById((Long) postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (!post.getMember().getId().equals(memberId)) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
            return false;
        }
        return true;
    }

    public boolean hasPermissionToPostMember(Long postId, Authentication authentication) {
        // 로그인이 안 되어 있거나, 익명 사용자인 경우 예외 발생
        Long memberId = memberService.authMemberId();
        var post = postRepository.findById((Long) postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getMember().getId()!=memberId) {
            if (!blogService.isBlogMember(post.getBlog(), memberId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
            }
        }

        return true;
    }

    public boolean createLike(Long postId) {
        Member member = memberService.getById(blogService.authMemberId());

        //좋아요한 게시물인지 체크
        if(isNotAlreadyLike(member.getId(), postId).isEmpty()) {
            Post post = postRepository.getReferenceById(postId);
            likeyRepository.save(Likey.builder()
                            .member(member)
                            .post(post)
                    .build());
            return true;
        }
        return false;
    }

    public boolean deleteLike(Long postId) {
        Member member = memberService.getById(blogService.authMemberId());
        // 좋아요한 게시물인지 체크
        Likey likey = likeyRepository.findByMemberIdAndPostId(member.getId(), postId).orElse(null);
        if (likey != null) {
            likeyRepository.delete(likey);
            return true;
        }

        return false;
    }

    //사용자가 이미 좋아요 한 게시물인지 체크
    private Optional<Likey> isNotAlreadyLike(Long memberId, Long postId) {
        return likeyRepository.findByMemberIdAndPostId(memberId, postId);
    }

    public Page<MainGridResponse> selectMainPosts(MainGridCondition condition, Pageable pageable) {
        Category category = categoryService.getReferenceById(condition.getCategoryId());
        Page<MainGridResponse> response = null;
        if (category == null || category.getParent() == null) {
        //categoryId가 부모 > 자식의 부모와 같은것 조회(limit 6)
            response = postRepository.selectMainPosts(condition, pageable);
        } else {
        //categoryId가 자식 > 자식인것만 조회
            response = postRepository.selectMainPostsCategory(condition, category, pageable);
        }

        //이미지 url 설정
        response.getContent().forEach(mainGridResponse ->
                mainGridResponse.getPostList().forEach(MainGridResponse.PostDto::processImgUrl)
        );

        return response;
    }

    public Page<PostListResponseDto> searchPostsMain(String keyword, Pageable pageable) {
        if(keyword == null) keyword = "";
        Page<PostListResponseDto> posts = postRepository.searchPostsMain(keyword, pageable);
        List<PostListResponseDto> newPosts = toPostList(posts);

        return new PageImpl<>(newPosts, pageable, posts.getTotalElements());
    }

    @Transactional
    public void deletePostsForAdmin(List<Long> postIds) {
        for (Long postId : postIds) {
            deletePost(postId);
        }
    }
}
