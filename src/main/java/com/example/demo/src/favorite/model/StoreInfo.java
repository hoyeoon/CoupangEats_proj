package com.example.demo.src.favorite.model;


import com.example.demo.src.store.model.GetStoreImageRes;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StoreInfo {
    @NonNull private int storeId;
    @NonNull private String storeImageUrl;
    @NonNull private String storeName;
    @NonNull private String cheetahDeliveryYn;
    private String reviewScoreAndCount;
    @NonNull private String distance;
    @NonNull private String deliveryTime;
    @NonNull private String deliveryFee;
}
