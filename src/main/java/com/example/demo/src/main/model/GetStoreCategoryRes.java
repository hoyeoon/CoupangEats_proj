package com.example.demo.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreCategoryRes {
    private int storeCategoryId;
    private String storeCategoryImageUrl;
    private String storeCategoryName;
}
