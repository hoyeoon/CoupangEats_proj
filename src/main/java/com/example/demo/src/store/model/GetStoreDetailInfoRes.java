package com.example.demo.src.store.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GetStoreDetailInfoRes {
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private String phone;
    @NonNull private String address;
    @NonNull private String ownerName;
    @NonNull private String businessLicenseNo;
    @NonNull private String businessName;
    @NonNull private String operatingTime;
    @NonNull private String introduction;
    private String notice;
    @NonNull private String countryOfOrigin;
}