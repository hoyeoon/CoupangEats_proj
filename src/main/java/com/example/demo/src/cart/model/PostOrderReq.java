package com.example.demo.src.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostOrderReq {
    private Integer couponId;
    private Integer accountId;
    private String toOwnerMessage;
    private String recycledProductYn;
    private String toRiderMessage;
}