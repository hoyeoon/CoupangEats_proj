package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private Integer orderInfoId;
    private Integer reviewScore;
    private String reviewDescription;
    private List<ReviewImage> reviewImages;
    private List<OrderMenuEvaluation> orderMenuEvaluations;
    private String riderLikeYn;
}