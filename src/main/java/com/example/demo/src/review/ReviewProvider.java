package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ReviewProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final JwtService jwtService;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }

    public int checkUserStatusByUserId(int userId) throws BaseException {
        try {
            return reviewDao.checkUserStatusByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkOrderInfoId(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.checkOrderInfoId(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkExistReview(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.checkExistReview(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getStoreId(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.getStoreId(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkOrderMenuId(int orderMenuId, PostReviewReq postReviewReq) throws BaseException {
        try {
            return reviewDao.checkOrderMenuId(orderMenuId, postReviewReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewId(int userId, int reviewId) throws BaseException {
        try {
            return reviewDao.checkReviewId(userId, reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserReviewRes getUserReviewRes(int userId, int reviewId) throws BaseException {
        try {
            return reviewDao.getUserReviewRes(userId, reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<ReviewImageInfo> reviewImageList(int reviewId) throws BaseException {
        try {
            return reviewDao.reviewImageList(reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<ReviewMenuInfo> reviewMenuInfoList(int reviewId) throws BaseException {
        try {
            return reviewDao.reviewMenuInfoList(reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkStoreId(int storeId) throws BaseException {
        try {
            return reviewDao.checkStoreId(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetReviewRes getReviewRes(int storeId) throws BaseException {
        try {
            return reviewDao.getReviewRes(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Review> getReviews(int storeId, int filter) throws BaseException {
        try {
            List<Review> getReviews;

            if(filter == 1){
                getReviews = reviewDao.getReviews(storeId, "order by r.updatedAt desc");
            } else if(filter == 2){
                getReviews = reviewDao.getReviews(storeId, "order by r.helpCount desc");
            } else if(filter == 3){
                getReviews = reviewDao.getReviews(storeId, "order by r.score desc");
            } else{
                getReviews = reviewDao.getReviews(storeId, "order by r.score");
            }

            for(Review review : getReviews){
                review.setReviewImageList(reviewDao.reviewImageList(review.getReviewId()));
                review.setReviewMenuInfoList(reviewDao.reviewMenuInfoList(review.getReviewId()));
            }
            return getReviews;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Review> getReviewByPhoto(int storeId, int filter) throws BaseException {
        try {
            List<Review> getReviewByPhoto;

            if(filter == 1){
                getReviewByPhoto = reviewDao.getReviewByPhoto(storeId, "order by r.updatedAt desc");
            } else if(filter == 2){
                getReviewByPhoto = reviewDao.getReviewByPhoto(storeId, "order by r.helpCount desc");
            } else if(filter == 3){
                getReviewByPhoto = reviewDao.getReviewByPhoto(storeId, "order by r.score desc");
            } else{
                getReviewByPhoto = reviewDao.getReviewByPhoto(storeId, "order by r.score");
            }

            for(Review review : getReviewByPhoto){
                review.setReviewImageList(reviewDao.reviewImageList(review.getReviewId()));
                review.setReviewMenuInfoList(reviewDao.reviewMenuInfoList(review.getReviewId()));
            }
            return getReviewByPhoto;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}