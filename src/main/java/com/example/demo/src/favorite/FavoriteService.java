package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional(rollbackFor = BaseException.class)
public class FavoriteService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteDao favoriteDao;
    private final FavoriteProvider favoriteProvider;
    private final JwtService jwtService;

    @Autowired
    public FavoriteService(FavoriteDao favoriteDao, FavoriteProvider favoriteProvider, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.favoriteProvider = favoriteProvider;
        this.jwtService = jwtService;
    }

    public void createFavorite(int userId, int storeId) throws BaseException {
        try {
            favoriteDao.createFavorite(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyFavoriteStatus(int userId, int storeId) throws BaseException {
        try {
            if(favoriteProvider.checkFavoriteActiveStatus(userId, storeId).equals("N")){
                favoriteDao.modifyFavoriteActiveStatusY(userId, storeId);
            }
            else if(favoriteProvider.checkFavoriteActiveStatus(userId, storeId).equals("Y")){
                favoriteDao.modifyFavoriteActiveStatusN(userId, storeId);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
