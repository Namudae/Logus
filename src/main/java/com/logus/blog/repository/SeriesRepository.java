package com.logus.blog.repository;

import com.logus.blog.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByBlogIdOrderBySeriesOrder(Long blogId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Series s WHERE s.blog.id = :blogId")
    void bulkDeleteByBlogId(Long blogId);
}
