package com.logus.blog.service;

import com.logus.admin.repository.ReportRepository;
import com.logus.blog.dto.*;
import com.logus.blog.entity.*;
import com.logus.blog.repository.*;
import com.logus.common.entity.AttachmentType;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.UserPrincipal;
import com.logus.common.service.S3Service;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@Service
@RequiredArgsConstructor
public class BlogFacadeService {

    private final PostService postService;
    private final BlogService blogService;
    private final MemberService memberService;
    private final SeriesService seriesService;
    private final S3Service s3Service;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SeriesRepository seriesRepository;
    private final FollowRepository followRepository;
    private final VisitRepository visitRepository;
    private final BlogMemberRepository blogMemberRepository;
    private final BlogRepository blogRepository;
    private final LikeyRepository likeyRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    /**
     * 블로그 삭제(회원 탈퇴시 사용)
     * - 게시글O
     * - 시리즈O
     * - 팔로우O
     * - 방문O
     * - 블로그멤버O
     * - 블로그O
     */
    @Transactional
    public void deleteBlog(Long blogId) {
        Blog blog = blogService.getById(blogId);

        //게시글
        List<Post> posts = postRepository.findByBlogId(blogId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //시리즈
        List<Series> series = seriesRepository.findByBlogIdOrderBySeriesOrder(blogId);
        for (Series s : series) {
            if (s.getImgUrl() != null || !s.getImgUrl().equals("")) {
                s3Service.deleteS3(s.getImgUrl());
            }
        }
        seriesRepository.bulkDeleteByBlogId(blogId);
        //팔로우
        followRepository.bulkDeleteByBlogId(blogId);
        //방문
        visitRepository.bulkDeleteByBlogId(blogId);
        //블로그
        blogRepository.delete(blog);
        //블로그멤버
        blogMemberRepository.bulkDeleteByBlogId(blogId);
    }

    /**
     * 블로그 초기화(블로그 삭제시 사용)
     */
    @Transactional
    public void resetBlog(Long blogId) {
        Blog blog = blogService.getById(blogId);

        //게시글
        List<Post> posts = postRepository.findByBlogId(blogId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //시리즈
        List<Series> series = seriesRepository.findByBlogIdOrderBySeriesOrder(blogId);
        for (Series s : series) {
            if (s.getImgUrl() != null || !s.getImgUrl().equals("")) {
                s3Service.deleteS3(s.getImgUrl());
            }
        }
        seriesRepository.bulkDeleteByBlogId(blogId);
        //팔로우
        followRepository.bulkDeleteByBlogId(blogId);
        //방문
        visitRepository.bulkDeleteByBlogId(blogId);
        if ("Y".equals(blog.getShareYn())) {
            //블로그
            blogRepository.delete(blog);
            //블로그멤버
            blogMemberRepository.bulkDeleteByBlogId(blogId);
        }
    }

    @Transactional
    public SeriesResponseDto createSeries(SeriesRequestDto seriesRequestDto, MultipartFile img) throws IOException {
        Blog blog = blogService.getReferenceById(seriesRequestDto.getBlogId());

        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.imgUpload(img, AttachmentType.SERIES);
        }

        Series series = seriesRequestDto.toEntity(blog, imgUrl);
        seriesRepository.save(series);

        if (imgUrl == null || imgUrl.isEmpty()) {
            imgUrl = null;
        } else {
            imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
        }
        return new SeriesResponseDto(series.getId(), imgUrl);
    }

    @Transactional
    public SeriesResponseDto updateSeries(Long seriesId, SeriesRequestDto seriesRequestDto, MultipartFile img, boolean deleteImg) throws IOException {
        Series series = seriesService.getById(seriesId);

        //이미지 처리
        if (deleteImg) {
            //기존 썸네일 처리(s3 삭제, ImgUrl 지우기)
            s3Service.deleteS3(series.getImgUrl());
            series.deleteImgUrl();
        }
        //이미지 업로드
        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.imgUpload(img, AttachmentType.SERIES);
        }
        series.updateSeries(seriesRequestDto, imgUrl);

        if (imgUrl == null || imgUrl.isEmpty()) {
            imgUrl = null;
        } else {
            imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
        }
        return new SeriesResponseDto(series.getId(), imgUrl);
    }

    @Transactional
    public void deleteSeries(Long seriesId) {
        Series series = seriesService.getById(seriesId);
        //이미지 삭제
        if (series.getImgUrl() != null) {
            s3Service.deleteS3(series.getImgUrl());
        }
        //seriesId 포함된 post 수정
        postRepository.bulkUpdatePostBySeriesId(seriesId);

        seriesRepository.delete(series);
    }

    @Transactional
    public void updateSeriesOrder(SeriesOrderRequestDto seriesOrderRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        blogService.validateAuthentication(authentication);
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        for (SeriesOrderRequestDto.SeriesDto seriesDto : seriesOrderRequestDto.getSeriesList()) {
            Series series = seriesService.getById(seriesDto.getSeriesId());
            blogService.hasPermissionToBlogUserPrincipal(series.getId(), "SERIES", "ADMIN", userPrincipal);
            series.updateSeries(seriesDto);
        }
    }


    public Page<FollowResponseDto> selectFollow(Pageable pageable) {
        Long memberId = blogService.authMemberId();

        return blogRepository.selectFollows(memberId, pageable);
    }

    public void createFollow(Long blogId) {
        Long memberId = blogService.authMemberId();

        Blog blog = blogService.getById(blogId);
        Member member = memberService.getById(memberId);

        Follow follow = Follow.builder()
                .blog(blog)
                .member(member)
                .build();
        followRepository.save(follow);
    }

    public void deleteFollow(Long followId) {
        Long memberId = blogService.authMemberId();
        Follow follow = followRepository.getReferenceById(followId);
        if (follow.getMember().getId() != memberId) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        followRepository.delete(follow);
    }

    public Page<FollowerResponseDto> selectFollower(Long blogId, Pageable pageable) {
        Page<FollowerResponseDto> followers = blogRepository.selectFollowers(blogId, pageable);

        // 각 FollowerResponseDto의 imgUrl을 가공
        followers.forEach(FollowerResponseDto::processImgUrl);

        return followers;
    }

    public void deleteFollower(Long followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        blogService.validateAuthentication(authentication);
        Follow follow = followRepository.getReferenceById(followId);
        blogService.hasPermissionToBlog(follow.getBlog().getId(), "BLOG", "ADMIN", authentication);
        followRepository.delete(follow);
    }

    public List<OurLogResponseDto> selectOurLog() {
        Long memberId = blogService.authMemberId();
        List<OurLogResponseDto> dtos = blogRepository.findByMemberId(memberId);

        // 각 OurLogResponseDto의 imgUrl을 가공
        dtos.forEach(OurLogResponseDto::changeImgUrl);
        return dtos;
    }


    @Transactional
    public Long updateBlog(Long blogId, BlogRequestDto blogRequestDto) {
        Blog blog = blogService.getById(blogId);

        //blogAddress 중복체크
        if (!blog.getBlogAddress().equals(blogRequestDto.getBlogAddress())) {
            blogService.duplicateBlogAddress(blogRequestDto.getBlogAddress());
        }

        //OWNER 체크
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (blogRequestDto.getBlogMembers() != null) {
            if (blogService.hasPermissionToBlog(blogId, "BLOG", "OWNER", authentication)) {
                //블로그 멤버
                //oldMember 중 new에 없는 멤버 delete
                List<BlogMember> oldBlogMembers = blogMemberRepository.findByBlogId(blogId);
                List<BlogMemberRequestDto> newBlogMembers = blogRequestDto.getBlogMembers();

                // 기존 블로그 멤버 ID
                Set<Long> newMemberIds = newBlogMembers.stream()
                        .map(BlogMemberRequestDto::getMemberId)
                        .collect(Collectors.toSet());

                // 1. 기존 멤버 삭제: newBlogMembers에 없는 oldBlogMembers 삭제
                oldBlogMembers.stream()
                        .filter(oldMember -> !newMemberIds.contains(oldMember.getMember().getId())
                                && !oldMember.getBlogAuth().equals(BlogAuth.OWNER)) //소유자 삭제X
                        .forEach(blogMemberRepository::delete);

                // 2. 새로운 멤버 추가: 기존에 없는 멤버는 새로 추가
                newBlogMembers.stream()
                        .filter(newMember -> oldBlogMembers.stream()
                                .noneMatch(oldMember -> oldMember.getMember().getId().equals(newMember.getMemberId())))
                        .forEach(newMember -> {
                            Member member = memberService.getById(newMember.getMemberId());
                            BlogMember blogMember = newMember.toEntity(member, blog, BlogAuth.EDITOR);
                            blogMemberRepository.save(blogMember);
                        });
            }
        }

        blog.updateBlogInfo(blogRequestDto);
        return blogId;
    }

    public List<BlogMemberResponseDto> updateBlogAuth(Long blogId, List<BlogMemberRequestDto> blogMemberRequestDto) {
        Blog blog = blogService.getById(blogId);

        blogMemberRequestDto.stream()
                .filter(updateMember -> updateMember.getBlogAuth() != BlogAuth.OWNER)  // OWNER 제외
                .forEach(updateMember -> {
                    Member member = memberService.getById(updateMember.getMemberId());
                    BlogMember existingMember = blogMemberRepository.findByBlogAndMember(blog, member);

                    if (existingMember != null && existingMember.getBlogAuth() != BlogAuth.OWNER) {
                        // 기존 멤버의 blogAuth가 OWNER가 아닌 경우에만 업데이트 수행
                        existingMember.updateBlogAuth(updateMember.getBlogAuth());
                        blogMemberRepository.save(existingMember);
                    }
                });

        return null;
    }

    @Transactional
    public Long createBlog(BlogRequestDto blogRequestDto) {
        Long memberId = blogService.authMemberId();

        //blogAddress 중복체크
        blogService.duplicateBlogAddress(blogRequestDto.getBlogAddress());

        Blog blog = blogRequestDto.toEntity();
        Blog savedBlog = blogRepository.save(blog);

        saveBlogMembers(blogRequestDto.getBlogMembers(), savedBlog, memberId);

        return savedBlog.getId();
    }


    @Transactional
    private void saveBlogMembers(List<BlogMemberRequestDto> blogMembers, Blog savedBlog, Long ownerId) {
        if (blogMembers != null) {
            for (BlogMemberRequestDto blogMemberRequestDto : blogMembers) {
                Member member = memberService.getById(blogMemberRequestDto.getMemberId());
                BlogAuth blogAuth = null;
                if (blogMemberRequestDto.getMemberId() == ownerId) {
                    blogAuth = BlogAuth.OWNER;
                } else {
                    blogAuth = BlogAuth.EDITOR;
                }
                BlogMember blogMember = blogMemberRequestDto.toEntity(member, savedBlog, blogAuth);
                blogMemberRepository.save(blogMember);
            }
        }
    }

    /**
     * - 내가 OWNER인 블로그 삭제(다른 멤버가 있는 경우 OWNER 양도)O
     * - 내가 포함된 blogMember 모두 삭제O
     * - 내가 쓴 글 삭제
     * - 내가 쓴 댓글 삭제
     * - 좋아요 삭제
     * - 신고 삭제X
     * - 팔로우 삭제
     * - 방문 삭제 > 블로그 방문만
     */
    @Transactional
    public Long deleteMember(Long memberId) {
//        Member member = memberService.getById(memberService.authMemberId());
        Member member = memberService.getById(memberId);
        //내가 OWNER인 블로그 처리
        processOwnBlog(member);
        //내가 포함된 blogMember 삭제
        blogMemberRepository.bulkDeleteByMemberId(member.getId());
        //내가 쓴 글 삭제
        List<Post> posts = postRepository.findByMemberId(member.getId());
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        //내가 쓴 댓글 삭제
        commentRepository.bulkDeleteByMemberId(member.getId());
        //내가 누른 좋아요 삭제
        likeyRepository.bulkDeleteByMemberId(member.getId());
        //내가 작성한 신고&내가 당한 신고
//        reportRepository.bulkDeleteReporterByMemberId(member.getId());
//        reportRepository.bulkDeleteReportedByMemberId(member.getId());
        //팔로우 삭제
        followRepository.bulkDeleteByMemberId(member.getId());
        //프사 삭제
        if (member.getImgUrl() != null && !member.getImgUrl().equals("")) {
            s3Service.deleteS3(member.getImgUrl());
        }
        //멤버 삭제
        memberRepository.delete(member);

        return member.getId();
    }

    private void processOwnBlog(Member member) {
        List<Blog> ownblogs = blogRepository.ownedBlogs(member);
        for (Blog ownblog : ownblogs) {
            if ("N".equals(ownblog.getShareYn())) {
                deleteBlog(ownblog.getId());
                continue;
            }

            //shareYn=Y인 경우, 다른 멤버가 있다면 Admin, createDate 순으로 정렬하여 blogMember Owner로 변경
            List<BlogMember> blogMembers = blogMemberRepository.findByBlogId(ownblog.getId());
            if (blogMembers.size()==1) {
                deleteBlog(ownblog.getId());
                continue;
            }

            // blogAuth와 createDate를 기준으로 정렬
            List<BlogMember> sortedBlogMembers = blogMembers.stream()
                    .sorted(Comparator.comparing((BlogMember bm) -> {
                                if (BlogAuth.ADMIN.equals(bm.getBlogAuth())) return 1;
                                if (BlogAuth.EDITOR.equals(bm.getBlogAuth())) return 2;
                                return 3; // 기타 권한
                            })
                            .thenComparing(BlogMember::getCreateDate))
                    .toList();

            // 정렬된 리스트에서 첫 번째 사용자를 Owner로 설정
            if (!sortedBlogMembers.isEmpty()) {
                BlogMember newOwner = sortedBlogMembers.get(0);
                newOwner.updateBlogAuth(BlogAuth.OWNER);
                blogMemberRepository.save(newOwner);
            }
        }
    }
}

