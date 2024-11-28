package com.logus.blog.repository;

import com.logus.blog.entity.Series;

import java.util.List;

public interface SeriesRepositoryCustom {
    List<Series> findByBlogIdOrderBySeriesOrder(Long blogId);
}
