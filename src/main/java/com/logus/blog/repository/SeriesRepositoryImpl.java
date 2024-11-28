package com.logus.blog.repository;

import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Series;
import com.logus.blog.entity.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QPost.post;
import static com.logus.blog.entity.QSeries.series;

public class SeriesRepositoryImpl implements SeriesRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public SeriesRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Series> findByBlogIdOrderBySeriesOrder(Long blogId) {

        return jpaQueryFactory
                .select(series)
                .from(series)
                .leftJoin(series.blog, blog)
                .where(series.blog.id.eq(blogId))
                .orderBy(series.seriesOrder.asc().nullsLast(), series.id.asc())
                .fetch();
    }
}
