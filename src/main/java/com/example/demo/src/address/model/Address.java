package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Address {
    private int userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String title;
    private String roadNameAddress;
    private String detailAddress;
    private String guideRoad;
    private String addressType;
    private String nickname;
}