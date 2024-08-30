package com.logus.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class CommonCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "common_code_id")
    private Long id;

    @Column(length = 20)
    private String codeType;

    @Column(length = 30)
    private String codeName;

    @Column(length = 100)
    private String codeExplain;
}
