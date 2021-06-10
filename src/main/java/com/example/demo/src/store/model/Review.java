package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Review {
    private int reviewId;
    private String reviewImageUrl;
    private String reviewDescription;
    private int reviewScore;
}