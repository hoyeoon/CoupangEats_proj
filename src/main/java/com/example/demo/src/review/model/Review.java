package com.example.demo.src.review.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Review {
    @NonNull private int reviewId;
    @NonNull private String userName;
    @NonNull private int score;
    @NonNull private String updatedAt;
    private List<ReviewImageInfo> reviewImageList;
    @NonNull private String description;
    private List<ReviewMenuInfo> reviewMenuInfoList;
}
