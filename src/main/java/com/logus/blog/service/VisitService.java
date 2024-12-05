package com.logus.blog.service;

import com.logus.blog.dto.VisitBlogDto;
import com.logus.blog.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitBlogDto selectBlogStastics(Long blogId) {
        List<VisitBlogDto.BlogVisitorDto> visitors = visitRepository.selectBlogMonthVisit(blogId);
        VisitBlogDto dto = visitRepository.selectTodayYesterdayTotal(blogId);
        dto.setBlogVisitors(visitors);

        return dto;
    }
}
