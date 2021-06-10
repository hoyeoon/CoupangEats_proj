package com.example.demo.src.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPriceRes {
    private String orderPrice;
    private String deliveryFee;
    private String totalPrice;
}
