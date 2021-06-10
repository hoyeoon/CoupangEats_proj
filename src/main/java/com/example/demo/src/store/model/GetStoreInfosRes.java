package com.example.demo.src.store.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class GetStoreInfosRes {
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private double totalReviewScore;
    @NonNull private String totalReviewCount;
    @NonNull private String deliveryTime;
    @NonNull private String newStoreYn;
    @NonNull private String cheetahDeliveryYn;
    private String deliveryFee;
    private String minOrderPrice;
}
