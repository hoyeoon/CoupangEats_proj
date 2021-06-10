package com.example.demo.src.store.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetStoreRes {
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private String newStoreYn;
    @NonNull private String cheetahDeliveryYn;
    @NonNull private String deliveryTime;
    private String reviewScoreAndCount;
    @NonNull private String distance;
    @NonNull private String deliveryFee;
    private List<GetStoreImageRes> storeImageList;
}
