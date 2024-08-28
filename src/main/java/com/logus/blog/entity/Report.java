package com.logus.blog.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nonapi.io.github.classgraph.json.JSONUtils;

import java.time.LocalDateTime;

@Entity
@Getter
public class Report {

    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "member_id", name = "reporter_member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "member_id", name = "reported_member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, columnDefinition = "varchar(30)")
    private ReportType reportType;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private Status status;

    private LocalDateTime createDate;

    private LocalDateTime solutionDate;

}
