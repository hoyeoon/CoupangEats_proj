package com.example.demo.src.store.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class MenuCategory {
    @NonNull private int menuCategoryId;
    @NonNull private String menuCategoryName;
    private List<Menu> menuList;
}
