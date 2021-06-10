package com.example.demo.src.cart;

import com.example.demo.config.BaseException;
import com.example.demo.src.cart.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class CartProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CartDao cartDao;
    private final JwtService jwtService;

    @Autowired
    public CartProvider(CartDao cartDao, JwtService jwtService) {
        this.cartDao = cartDao;
        this.jwtService = jwtService;
    }

    public int checkUserStatusByUserId(int userId) throws BaseException {
        try {
            return cartDao.checkUserStatusByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMenuInfoRes getMenuInfoRes(int menuId) throws BaseException {
        try {
            return cartDao.getMenuInfoRes(menuId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMenuPriceRes getMenuPriceRes(int menuId, int count) throws BaseException {
        try {
            return cartDao.getMenuPriceRes(menuId, count);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMenuId(int menuId) throws BaseException {
        try {
            return cartDao.checkMenuId(menuId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCartId(int userId) throws BaseException {
        try {
            return cartDao.checkCartId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCartMenuId(int userId, int menuId) throws BaseException {
        try {
            return cartDao.checkCartMenuId(userId, menuId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkSameStoreMenu(int userId, int menuId) throws BaseException {
        try {
            return cartDao.checkSameStoreMenu(userId, menuId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getCartId(int userId) throws BaseException {
        try {
            return cartDao.getCartId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCartMenuIdByUserId(int userId) throws BaseException {
        try {
            return cartDao.checkCartMenuIdByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCartMenuIdByCartMenuId(int userId, int cartMenuId) throws BaseException {
        try {
            return cartDao.checkCartMenuIdByCartMenuId(userId, cartMenuId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetAddressRes getAddressRes(int userId) throws BaseException {
        try{
            return cartDao.getAddressRes(userId);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreInfoRes getStoreInfoRes(int userId) throws BaseException {
        try {
            return cartDao.getStoreInfoRes(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetCartMenusRes> getCartMenusRes(int userId) throws BaseException {
        try {
            return cartDao.getCartMenusRes(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetDiscountCouponRes> getDiscountCouponRes(int userId) throws BaseException {
        try {
            List<GetDiscountCouponRes> getDiscountCouponRes = cartDao.getDiscountCouponRes(userId);
            for(GetDiscountCouponRes discountCouponRes : getDiscountCouponRes){
                if(cartDao.checkUsableCoupon(userId, discountCouponRes.getCouponId()) == 1){
                    discountCouponRes.setIsUsableCouponYn("Y");
                } else{
                    discountCouponRes.setIsUsableCouponYn("N");
                }
            }
            return getDiscountCouponRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getOrderPrice(int userId) throws BaseException {
        try {
            return cartDao.getOrderPrice(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getDeliveryFee(int userId) throws BaseException {
        try {
            return cartDao.getDeliveryFee(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getTotalPrice(int userId) throws BaseException {
        try {
            int totalPriceIntValue = cartDao.getOrderPriceIntValue(userId) + cartDao.getDeliveryFeeIntValue(userId);
            DecimalFormat dc = new DecimalFormat("###,###,###,###");
            String totalPrice = dc.format(totalPriceIntValue) + '원';
            return totalPrice;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getTotalPriceIntValue(int userId) throws BaseException {
        try {
            return cartDao.getOrderPriceIntValue(userId) + cartDao.getDeliveryFeeIntValue(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkBalanceWithCoupon(int userId, int accountId, int totalPrice, int couponId) throws BaseException {
        try {
            return cartDao.checkBalanceWithCoupon(userId, accountId, totalPrice, couponId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkBalance(int userId, int accountId, int totalPrice) throws BaseException {
        try {
            return cartDao.checkBalance(userId, accountId, totalPrice);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getUserAddress(int userId) throws BaseException{
        try {
            return cartDao.getUserAddress(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getPaymentMethod(int userId, int accountId) throws BaseException{
        try {
            return cartDao.getPaymentMethod(userId, accountId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<CartMenus> cartMenusList(int userId) throws BaseException{
        try {
            return cartDao.cartMenusList(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkAccount(int userId, int accountId) throws BaseException{
        try {
            return cartDao.checkAccount(userId, accountId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUsableCoupon(int userId, int couponId) throws BaseException{
        try {
            return cartDao.checkUsableCoupon(userId, couponId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getStoreIdByUserId(int userId) throws BaseException{
        try {
            return cartDao.getStoreIdByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}