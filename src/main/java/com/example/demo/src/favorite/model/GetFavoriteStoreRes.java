package com.example.demo.src.favorite.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFavoriteStoreRes {
    private String totalCount;
    private String filter;
    private List<StoreInfo> storeInfoList;
}