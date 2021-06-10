package com.example.demo.src.review.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetUserReviewRes {
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private int score;
    @NonNull private String updatedAt;
    private List<ReviewImageInfo> reviewImages;
    @NonNull private String description;
    private List<ReviewMenuInfo> reviewMenuInfos;
    @NonNull private String helpCount;
    @NonNull private String remainingUpdateDate;
}