package com.logus.admin.repository;

import com.logus.admin.dto.CategoryResponseDto;
import com.logus.blog.dto.OurLogResponseDto;
import com.logus.blog.entity.BlogAuth;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.logus.admin.entity.QCategory.category;
import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CategoryRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CategoryResponseDto> selectCategory(Long parentId) {

        return jpaQueryFactory
                .select(Projections.fields(CategoryResponseDto.class,
                        category.id.as("categoryId"),
                        category.parent.id.as("parentId"),
                        category.orderSeq,
                        category.categoryName
                ))
                .from(category)
                .where(
                        parentId == null ? category.parent.isNull() : category.parent.id.eq(parentId)
                )
                .orderBy(category.orderSeq.asc())
                .fetch();

    }
}
