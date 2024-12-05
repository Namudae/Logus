package com.logus.blog.repository;

import com.logus.blog.entity.BlogMember;
import com.logus.blog.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface VisitRepository extends JpaRepository<Visit, Long>, VisitRepositoryCustom {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Visit v WHERE v.blog.id = :blogId")
    void bulkDeleteByBlogId(Long blogId);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.sessionId = :visitorId AND v.createDate = :visitDate")
    Long countByVisitorIdAndCreateDate(@Param("visitorId") String visitorId,
                                       @Param("visitDate") LocalDate visitDate);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.sessionId = :visitorId AND v.createDate = :visitDate AND v.blog.id = :blogId")
    Long countByVisitorIdAndCreateDateAndBlogId(@Param("visitorId") String visitorId,
                                                @Param("visitDate") LocalDate visitDate,
                                                @Param("blogId") Long blogId);

}
