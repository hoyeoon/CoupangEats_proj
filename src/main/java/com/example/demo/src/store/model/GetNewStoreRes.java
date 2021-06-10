package com.example.demo.src.store.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class GetNewStoreRes {
    @NonNull private int storeId;
    private String storeImageUrl;
    @NonNull private String deliveryTime;
    @NonNull private String storeName;
    @NonNull private String distance;
    @NonNull private String deliveryFee;
}