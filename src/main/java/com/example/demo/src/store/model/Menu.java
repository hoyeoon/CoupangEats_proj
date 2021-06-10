package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Menu {
    private int menuId;
    private String manyOrdersYn;
    private String reviewBestYn;
    private String menuName;
    private String menuPrice;
    private String menuDescription;
    private String menuImageUrl;
}
