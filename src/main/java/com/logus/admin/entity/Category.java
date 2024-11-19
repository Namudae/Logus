package com.logus.admin.entity;

import com.logus.admin.dto.CategoryOrderRequestDto;
import com.logus.admin.dto.CategoryRequestDto;
import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.SeriesOrderRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    //계층형
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true) //부모 카테고리 영향을 받음
    private List<Category> child = new ArrayList<>();

    private Integer orderSeq;

    @Column(length = 30)
    private String categoryName;

//    @OneToMany(mappedBy = "category")
//    private List<Post> posts = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

    //==비즈니스 로직==//
    //카테고리 수정
    public void updateCategory(CategoryRequestDto categoryRequestDto) {
        this.categoryName = categoryRequestDto.getCategoryName();
        this.orderSeq = categoryRequestDto.getOrderSeq();
    }

    //순서 일괄 수정
    public void updateCategory(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

}
