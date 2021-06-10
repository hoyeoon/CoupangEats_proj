package com.example.demo.src.review.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetReviewRes {
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private double totalReviewScore;
    @NonNull private String totalReviewCount;
    private String photoReviewYn;
    private String filter;
    private List<Review> reviewList;
}