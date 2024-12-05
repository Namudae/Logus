package com.logus.blog.repository;

import com.logus.blog.dto.VisitBlogDto;

import java.util.List;

public interface VisitRepositoryCustom {
    List<VisitBlogDto.BlogVisitorDto> selectBlogMonthVisit(Long blogId);
    VisitBlogDto selectTodayYesterdayTotal(Long blogId);
}
