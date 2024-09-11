package com.logus.common.entity;

import com.logus.blog.entity.Post;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private AttachmentType attachmentType;

    private String filename;

    private String orgFilename;

    private String filepath;

    @Builder
    public Attachment(Long id, AttachmentType attachmentType, String filename, String orgFilename, String filepath) {
        this.id = id;
        this.attachmentType = attachmentType;
        this.filename = filename;
        this.orgFilename = orgFilename;
        this.filepath = filepath;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
