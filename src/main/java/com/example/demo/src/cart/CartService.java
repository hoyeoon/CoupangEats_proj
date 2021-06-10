package com.example.demo.src.cart;

import com.example.demo.config.BaseException;
import com.example.demo.src.cart.model.CartMenus;
import com.example.demo.src.cart.model.PostCartReq;
import com.example.demo.src.cart.model.PostOrderReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional(rollbackFor = BaseException.class)
public class CartService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CartDao cartDao;
    private final CartProvider cartProvider;
    private final JwtService jwtService;

    @Autowired
    public CartService(CartDao cartDao, CartProvider cartProvider, JwtService jwtService) {
        this.cartDao = cartDao;
        this.cartProvider = cartProvider;
        this.jwtService = jwtService;
    }

    public void addToCart(int userId, PostCartReq postCartReq) throws BaseException {
        try{
            // 카트가 비어있을 때
            if (cartProvider.checkCartId(userId) == 0) {
                // 새로운 카트 생성
                int cartId = cartDao.makeCart(userId);
                // 카트 메뉴가 존재할 때, 기존 카트 메뉴에 count 만큼 증가
                if (cartProvider.checkCartMenuId(userId, postCartReq.getMenuId()) == 1) {
                    cartDao.modifyCartMenuCount(userId, postCartReq);
                }
                // 카트 메뉴가 존재하지 않을 때, 새로운 카트 메뉴 생성
                else {
                    cartDao.makeCartMenu(userId, cartId, postCartReq);
                }
            }
            // 카트가 존재할 때
            else {
                // 카트 메뉴가 존재할 때, 기존 카트 메뉴에 count 만큼 증가
                if (cartProvider.checkCartMenuId(userId, postCartReq.getMenuId()) == 1) {
                    cartDao.modifyCartMenuCount(userId, postCartReq);
                }
                // 카트 메뉴가 존재하지 않을 때, 새로운 카트 메뉴 생성
                else {
                    int cartId = cartProvider.getCartId(userId);
                    cartDao.makeCartMenu(userId, cartId, postCartReq);
                }
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCartMenu(int userId, int cartMenuId) throws BaseException {
        try{
            cartDao.deleteCartMenuByCartMenuId(userId, cartMenuId);
            if(cartProvider.checkCartMenuIdByUserId(userId) == 0){
                cartDao.deleteCartByUserId(userId);
            }
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCart(int userId) throws BaseException{
        try {
            cartDao.deleteCartMenuByUserId(userId);
            cartDao.deleteCartByUserId(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postOrderWithCoupon(PostOrderReq postOrderReq, int userId, int accountId, int couponId) throws BaseException{
        try{
            int storeId = cartProvider.getStoreIdByUserId(userId);
            int totalPrice = cartProvider.getTotalPriceIntValue(userId);
            String address = cartProvider.getUserAddress(userId);
            String paymentMethod = cartProvider.getPaymentMethod(userId, accountId);
            List<CartMenus> cartMenusList = cartProvider.cartMenusList(userId);

            cartDao.withdrawAccountWithCoupon(userId, accountId, totalPrice, couponId);
            cartDao.deleteCoupon(couponId);
            int orderInfoId = cartDao.makeOrderInfo(userId, storeId, address, paymentMethod, postOrderReq);

            for(CartMenus cartMenu : cartMenusList){
                cartDao.makeOrderInfoPrice(orderInfoId, cartMenu.getMenuId(), cartMenu.getCount());
            }
            cartDao.deleteCartMenuByUserId(userId);
            cartDao.deleteCartByUserId(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postOrder(PostOrderReq postOrderReq, int userId, int accountId) throws BaseException{
        try{
            int storeId = cartProvider.getStoreIdByUserId(userId);
            int totalPrice = cartProvider.getTotalPriceIntValue(userId);
            String address = cartProvider.getUserAddress(userId);
            String paymentMethod = cartProvider.getPaymentMethod(userId, accountId);
            List<CartMenus> cartMenusList = cartProvider.cartMenusList(userId);

            cartDao.withdrawAccount(userId, accountId, totalPrice);
            int orderInfoId = cartDao.makeOrderInfo(userId, storeId, address, paymentMethod, postOrderReq);
            for(CartMenus cartMenu : cartMenusList){
                cartDao.makeOrderInfoPrice(orderInfoId, cartMenu.getMenuId(), cartMenu.getCount());
            }
            cartDao.deleteCartMenuByUserId(userId);
            cartDao.deleteCartByUserId(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
