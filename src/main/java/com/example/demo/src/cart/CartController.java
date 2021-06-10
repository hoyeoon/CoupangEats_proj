package com.example.demo.src.cart;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.cart.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app")
public class CartController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CartService cartService;
    private final CartProvider cartProvider;
    private final JwtService jwtService;

    @Autowired
    public CartController(CartService cartService, CartProvider cartProvider, JwtService jwtService) {
        this.cartService = cartService;
        this.cartProvider = cartProvider;
        this.jwtService = jwtService;
    }

    /**
     * 카트 담기 화면 - 메뉴 정보 API
     * [GET] /:menuId/infos
     * @return BaseResponse<GetMenuInfoRes>
     */
    @ResponseBody
    @GetMapping("/menus/{menuId}/infos")
    public BaseResponse<GetMenuInfoRes> getMenuInfoRes(@PathVariable(required = false) String menuId) {
        if(menuId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(!isRegexInteger(menuId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(menuId);
            if(cartProvider.checkMenuId(id) == 0){
                return new BaseResponse<>(INVALID_MENU_ID);
            }
            return new BaseResponse<>(cartProvider.getMenuInfoRes(id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 담기 화면 - 수량에 따른 가격 API
     * [GET] /:menuId/price?count=
     * @return BaseResponse<getMenuPriceRes>
     */
    @ResponseBody
    @GetMapping("/menus/{menuId}/price")
    public BaseResponse<GetMenuPriceRes> getMenuPriceRes(@PathVariable(required = false) String menuId,
                                                         @RequestParam(required = false) String count) {
        if (menuId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        if (count == null) {
            return new BaseResponse<>(EMPTY_QUERY_PARAMS);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if (!isRegexInteger(menuId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int menu_id = Integer.parseInt(menuId);
            if (cartProvider.checkMenuId(menu_id) == 0) {
                return new BaseResponse<>(INVALID_MENU_ID);
            }

            if(!isRegexInteger(count)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int menu_count = Integer.parseInt(count);
            if(menu_count < 1){
                return new BaseResponse<>(INVALID_MENU_COUNT);
            }
            return new BaseResponse<>(cartProvider.getMenuPriceRes(menu_id, menu_count));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 담기 API
     * [POST] /carts
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/carts")
    public BaseResponse<String> addToCart(@RequestBody PostCartReq postCartReq) {
        if(postCartReq.getMenuId() == null){
            return new BaseResponse<>(POST_CARTS_EMPTY_MENU_ID);
        }
        if(postCartReq.getCount() == null){
            return new BaseResponse<>(POST_CARTS_EMPTY_COUNT);
        }
        if(postCartReq.getMenuId() < 1){
            return new BaseResponse<>(POST_CARTS_INVALID_MENU_ID);
        }
        if(postCartReq.getCount() < 1) {
            return new BaseResponse<>(POST_CARTS_INVALID_COUNT);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            // 카트가 존재할 때
            if (cartProvider.checkCartId(userIdByJwt) == 1){
                // 기존 카트에 담긴 메뉴와 새로 추가할 메뉴의 매장이 다른 경우
                if(cartProvider.checkSameStoreMenu(userIdByJwt, postCartReq.getMenuId()) == 0){
                    throw new BaseException(POST_CARTS_DIFFERENT_STORE);
                }
            }
            cartService.addToCart(userIdByJwt, postCartReq);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 메뉴 삭제 API
     * [DELETE] /carts/:cartMenuId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/carts/{cartMenuId}")
    public BaseResponse<String> deleteCartMenu(@PathVariable(required = false) String cartMenuId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            if(!isRegexInteger(cartMenuId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(cartMenuId);
            if(id < 1){
                return new BaseResponse<>(DELETE_CARTS_INVALID_CART_MENU_ID);
            }
            if(cartProvider.checkCartMenuIdByCartMenuId(userIdByJwt, id) == 0){
                return new BaseResponse<>(INVALID_CART_MENU_ID);
            }
            cartService.deleteCartMenu(userIdByJwt, id);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 전체 삭제 API
     * [DELETE] /carts
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/carts")
    public BaseResponse<String> deleteCart() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            cartService.deleteCart(userIdByJwt);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 주소 API
     * [GET] /carts/addresses
     * @return BaseResponse<GetAddressRes>
     */
    @ResponseBody
    @GetMapping("/carts/addresses")
    public BaseResponse<GetAddressRes> getAddressResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }

            return new BaseResponse<>(cartProvider.getAddressRes(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 매장 정보 API
     * [GET] /carts/store-infos
     * @return BaseResponse<GetStoreInfoRes>
     */
    @ResponseBody
    @GetMapping("/carts/store-infos")
    public BaseResponse<GetStoreInfoRes> getStoreInfoResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            return new BaseResponse<>(cartProvider.getStoreInfoRes(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 메뉴 목록 API
     * [GET] /carts/menus
     * @return BaseResponse<GetCartMenusRes>
     */
    @ResponseBody
    @GetMapping("/carts/menus")
    public BaseResponse<List<GetCartMenusRes>> getCartMenusResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            return new BaseResponse<>(cartProvider.getCartMenusRes(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 할인쿠폰 선택 화면 API
     * [GET] /carts/discount-coupons
     * @return BaseResponse<GetDiscountCouponRes>
     */
    @ResponseBody
    @GetMapping("/carts/discount-coupons")
    public BaseResponse<List<GetDiscountCouponRes>> getDiscountCouponResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            return new BaseResponse<>(cartProvider.getDiscountCouponRes(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 결제 금액 API
     * [GET] /carts/prices
     * @return BaseResponse<GetPriceRes>
     */
    @ResponseBody
    @GetMapping("/carts/prices")
    public BaseResponse<GetPriceRes> getPriceResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            GetPriceRes getPriceRes = new GetPriceRes(cartProvider.getOrderPrice(userIdByJwt),
                    cartProvider.getDeliveryFee(userIdByJwt), cartProvider.getTotalPrice(userIdByJwt));
            return new BaseResponse<>(getPriceRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 결제수단 API
     * [GET] /carts/payments/:accountId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/carts/payments/{accountId}")
    public BaseResponse<String> getPaymentMethod(@PathVariable(required = false) String accountId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            if(!isRegexInteger(accountId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(accountId);
            if(id < 1){
                return new BaseResponse<>(GET_CARTS_INVALID_ACCOUNT_ID);
            }
            if(cartProvider.checkAccount(userIdByJwt, id) == 0){
                return new BaseResponse<>(NOT_EXISTS_ACCOUNT_ID);
            }

            return new BaseResponse<>(cartProvider.getPaymentMethod(userIdByJwt, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 주문하기 API
     * [GET] /orders
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/orders")
    public BaseResponse<String> postOrders(@RequestBody PostOrderReq postOrderReq) {
        if(postOrderReq.getAccountId() == null){
            return new BaseResponse<>(POST_ORDERS_EMPTY_ACCOUNT_ID);
        }
        if(postOrderReq.getToOwnerMessage() == null){
            return new BaseResponse<>(POST_ORDERS_EMPTY_TO_OWNER_MESSAGE);
        }
        if(postOrderReq.getRecycledProductYn() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_RECYCLED_PRODUCT_YN);
        }
        if(!postOrderReq.getRecycledProductYn().equals("Y") && !postOrderReq.getRecycledProductYn().equals("N")){
            return new BaseResponse<>(POST_ORDERS_INVALID_RECYCLED_PRODUCT_YN);
        }
        if(postOrderReq.getToRiderMessage() == null){
            return new BaseResponse<>(POST_ORDERS_EMPTY_TO_RIDER_MESSAGE);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (cartProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            if(cartProvider.checkAccount(userIdByJwt, postOrderReq.getAccountId()) == 0){
                return new BaseResponse<>(NOT_EXISTS_ACCOUNT_ID);
            }

            if(postOrderReq.getCouponId() == null){
                if(cartProvider.checkBalance(userIdByJwt, postOrderReq.getAccountId(), cartProvider.getTotalPriceIntValue(userIdByJwt)) == 0){
                    return new BaseResponse<>(INSUFFICIENT_BALANCE);
                }
                cartService.postOrder(postOrderReq, userIdByJwt, postOrderReq.getAccountId());
            } else{
                if(cartProvider.checkUsableCoupon(userIdByJwt, postOrderReq.getCouponId()) == 0){
                    return new BaseResponse<>(INVALID_COUPON_ID);
                }

                if(cartProvider.checkBalanceWithCoupon(userIdByJwt, postOrderReq.getAccountId(), cartProvider.getTotalPriceIntValue(userIdByJwt), postOrderReq.getCouponId()) == 0){
                    return new BaseResponse<>(INSUFFICIENT_BALANCE);
                }
                cartService.postOrderWithCoupon(postOrderReq, userIdByJwt, postOrderReq.getAccountId(), postOrderReq.getCouponId());
            }
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
