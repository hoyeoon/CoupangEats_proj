package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.src.favorite.model.StoreInfo;
import com.example.demo.src.store.model.GCS;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FavoriteProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteDao favoriteDao;
    private final JwtService jwtService;

    @Autowired
    public FavoriteProvider(FavoriteDao favoriteDao, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.jwtService = jwtService;
    }

    public int checkUserStatusByUserId(int userId) throws BaseException {
        try {
            return favoriteDao.checkUserStatusByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkStore(int storeId) throws BaseException {
        try {
            return favoriteDao.checkStore(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkFavorite(int userId, int storeId) throws BaseException {
        try {
            return favoriteDao.checkFavorite(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkFavoriteByUserId(int userId) throws BaseException {
        try {
            return favoriteDao.checkFavoriteByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String checkFavoriteActiveStatus(int userId, int storeId) throws BaseException {
        try {
            return favoriteDao.checkFavoriteActiveStatus(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GCS getGCS(int userId) throws BaseException {
        try {
            return favoriteDao.getGCS(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<StoreInfo> getStoreInfos(BigDecimal userLatitude, BigDecimal userLongitude) throws BaseException {
        try {
            List<StoreInfo> storeInfoList = favoriteDao.storeInfoList(userLatitude, userLongitude);
            for(StoreInfo storeInfo : storeInfoList){
                storeInfo.setReviewScoreAndCount(favoriteDao.getReviewScoreAndCount(storeInfo.getStoreId()));
            }
            return storeInfoList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getTotalFavoriteCount(int userId) throws BaseException {
        try {
            return favoriteDao.getTotalFavoriteCount(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
