package com.example.demo.src.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMenuInfoRes {
    private int menuId;
    private String menuImageUrl;
    private String menuName;
    private String menuDescription;
}