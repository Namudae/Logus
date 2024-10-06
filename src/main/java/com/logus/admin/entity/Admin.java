package com.logus.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id", length = 20, unique = true)
    private String id; // 관리자 ID

    @Column(length = 20)
    private String name; // 관리자 이름

    @Column(length = 100)
    private String password; // 관리자 비밀번호

    @Embedded
    private Address address; // 주소 정보

    @Column(length = 15)
    private String phone; // 전화번호
}
