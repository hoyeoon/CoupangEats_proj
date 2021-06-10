package com.example.demo.src.cart.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreInfoRes {
    private int storeId;
    private String storeName;
    private String newStoreYn;
    private String cheetahDeliveryYn;
}