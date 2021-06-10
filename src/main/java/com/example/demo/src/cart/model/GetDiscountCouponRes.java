package com.example.demo.src.cart.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class GetDiscountCouponRes {
    @NonNull private int couponId;
    @NonNull private String couponName;
    @NonNull private String discountPrice;
    @NonNull private String minOrderPrice;
    @NonNull private String expireDate;
    private String isUsableCouponYn;
}