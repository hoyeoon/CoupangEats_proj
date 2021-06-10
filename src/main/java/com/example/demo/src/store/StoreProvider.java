package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class StoreProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final JwtService jwtService;

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    public int checkUserStatusByUserId(int userId) throws BaseException {
        try {
            return storeDao.checkUserStatusByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GCS getGCS(int userId) throws BaseException {
        try {
            return storeDao.getGCS(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetStoreRes> getStores(BigDecimal userLatitude, BigDecimal userLongitude) throws BaseException {
        try {
            List<GetStoreRes> storeResList = storeDao.getStores(userLatitude, userLongitude);
            for(GetStoreRes store : storeResList){
                store.setReviewScoreAndCount(storeDao.getReviewScoreAndCount(store.getStoreId()));
                store.setStoreImageList(storeDao.getStoreImages(store.getStoreId()));
            }
            return storeResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetStoreRes> getStoresByCategoryId(BigDecimal userLatitude, BigDecimal userLongitude, int categoryId) throws BaseException {
        try {
            List<GetStoreRes> storeResList = storeDao.getStoresByCategoryId(userLatitude, userLongitude, categoryId);
            for(GetStoreRes store : storeResList){
                store.setReviewScoreAndCount(storeDao.getReviewScoreAndCount(store.getStoreId()));
                store.setStoreImageList(storeDao.getStoreImages(store.getStoreId()));
            }
            return storeResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreRes> getStoresByKeyword(BigDecimal userLatitude, BigDecimal userLongitude, String storeName) throws BaseException {
        try {
            List<GetStoreRes> storeResList = storeDao.getStoresByKeyword(userLatitude, userLongitude, storeName);
            for(GetStoreRes store : storeResList){
                store.setReviewScoreAndCount(storeDao.getReviewScoreAndCount(store.getStoreId()));
                store.setStoreImageList(storeDao.getStoreImages(store.getStoreId()));
            }
            return storeResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreInfosRes getStoreInfos(int storeId) throws BaseException {
        try{
            GetStoreInfosRes getStoreInfosRes = storeDao.getStoreInfos(storeId);
            getStoreInfosRes.setDeliveryFee(storeDao.getDeliveryFee(storeId));
            getStoreInfosRes.setMinOrderPrice(storeDao.getMinOrderPrice(storeId));
            return getStoreInfosRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreImageRes> getStoreImages(int storeId) throws BaseException {
        try {
            return storeDao.getStoreImages(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<MenuCategory> getMenuCategories(int storeId) throws BaseException {
        try {
            List<MenuCategory> menuCategoryList = storeDao.getMenuCategories(storeId);
            for (MenuCategory menuCategory : menuCategoryList) {
                menuCategory.setMenuList(storeDao.getMenus(menuCategory.getMenuCategoryId()));
            }
            return menuCategoryList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Review> getStoreReviews(int storeId) throws BaseException {
        try {
            return storeDao.getStoreReviews(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetNewStoreRes> getNewStores(BigDecimal userLatitude, BigDecimal userLongitude) throws BaseException {
        try {
            List<GetNewStoreRes> newStoreResList = storeDao.getNewStores(userLatitude, userLongitude);
            for(GetNewStoreRes newStore : newStoreResList){
                newStore.setStoreImageUrl(storeDao.getStoreImageUrl(newStore.getStoreId()));
            }
            return newStoreResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetNewStoreRes> getNewStoresByCategoryId(BigDecimal userLatitude, BigDecimal userLongitude, int categoryId) throws BaseException {
        try {
            List<GetNewStoreRes> newStoreResList = storeDao.getNewStoresByCategoryId(userLatitude, userLongitude, categoryId);
            for(GetNewStoreRes newStore : newStoreResList){
                newStore.setStoreImageUrl(storeDao.getStoreImageUrl(newStore.getStoreId()));
            }
            return newStoreResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkStoreId(int storeId) throws BaseException {
        try {
            return storeDao.checkStoreId(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreDetailInfoRes getStoreDetailInfoRes(int storeId) throws BaseException {
        try {
            GetStoreDetailInfoRes getStoreDetailInfoRes = storeDao.getStoreDetailInfoRes(storeId);
            if(!storeDao.getStoreNotice(storeId).equals("")){
                getStoreDetailInfoRes.setNotice(storeDao.getStoreNotice(storeId));
            }
            return getStoreDetailInfoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetDeliveryPolicyInfoRes> getDeliveryPolicyInfoRes(int storeId) throws BaseException {
        try {
            return storeDao.getDeliveryPolicyInfoRes(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}