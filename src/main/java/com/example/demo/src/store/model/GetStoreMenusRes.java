package com.example.demo.src.store.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetStoreMenusRes {
    @NonNull private int storeId;
    private List<MenuCategory> menuCategoryList;
}