package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDeliveryPolicyInfoRes {
    private String orderPrice;
    private String deliveryFee;
}