package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewService reviewService;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewService = reviewService;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
    }

    /**
     * 만족도 평가 및 리뷰 작성 API
     * [POST] /reviews
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> postReviews(@RequestBody PostReviewReq postReviewReq) {
        if(postReviewReq.getOrderInfoId() == null){
            return new BaseResponse<>(POST_REVIEWS_EMPTY_ORDER_INFO_ID);
        }
        if(postReviewReq.getReviewDescription() == null){
            return new BaseResponse<>(POST_REVIEWS_EMPTY_DESCRIPTION);
        }
        if(!(postReviewReq.getReviewScore() >= 1 && postReviewReq.getReviewScore() <= 5)){
            return new BaseResponse<>(POST_REVIEWS_INVALID_SCORE);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (reviewProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(reviewProvider.checkOrderInfoId(userIdByJwt, postReviewReq.getOrderInfoId()) == 0){
                return new BaseResponse<>(INVALID_ORDER_INFO_ID);
            }
            if(reviewProvider.checkExistReview(userIdByJwt, postReviewReq.getOrderInfoId()) == 1){
                return new BaseResponse<>(EXISTS_REVIEW);
            }
            int storeId = reviewProvider.getStoreId(userIdByJwt, postReviewReq.getOrderInfoId());

            if(postReviewReq.getOrderMenuEvaluations() != null){
                List<OrderMenuEvaluation> orderMenuEvaluationList = postReviewReq.getOrderMenuEvaluations();
                for(OrderMenuEvaluation orderMenuEvaluation : orderMenuEvaluationList){
                    if(reviewProvider.checkOrderMenuId(orderMenuEvaluation.getOrderInfoMenuId(), postReviewReq) == 0){
                        return new BaseResponse<>(INVALID_ORDER_INFO_MENU_ID);
                    }
                }
            }

            int reviewId = reviewService.createReview(storeId, userIdByJwt, postReviewReq);

            if(postReviewReq.getReviewImages() != null){
                List<ReviewImage> reviewImageList = postReviewReq.getReviewImages();
                for(ReviewImage reviewImage : reviewImageList){
                    reviewService.createReviewImage(reviewId, reviewImage.getReviewImageUrl());
                }
            }

            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 내 리뷰 보기 API
     * [GET] /reviews/users/:reviewId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/users/{reviewId}")
    public BaseResponse<GetUserReviewRes> getMyReview(@PathVariable(required = false) String reviewId) {
        if (reviewId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (reviewProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }

            if (!isRegexInteger(reviewId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int review_id = Integer.parseInt(reviewId);
            if(reviewProvider.checkReviewId(userIdByJwt, review_id) == 0){
                return new BaseResponse<>(INVALID_REVIEW_ID);
            }

            GetUserReviewRes getUserReviewRes = reviewProvider.getUserReviewRes(userIdByJwt, review_id);
            getUserReviewRes.setReviewImages(reviewProvider.reviewImageList(review_id));
            getUserReviewRes.setReviewMenuInfos(reviewProvider.reviewMenuInfoList(review_id));

            return new BaseResponse<>(getUserReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 매장 리뷰 보기 API
     * [GET] /reviews/:storeId
     *
     * 매장 리뷰 보기 API + 포토 리뷰 + 정렬
     * [GET] /reviews/:storeId?isPhotoReview=&filter=
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping({"/{storeId}", ""})
    public BaseResponse<GetReviewRes> getReviews(@PathVariable(required = false) String storeId,
                                                 @RequestParam(required = false) String isPhotoReview,
                                                 @RequestParam(required = false) Integer filter) {
        if(storeId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        if(filter == null){
            filter = 1;
        }
        if(isPhotoReview == null){
            isPhotoReview = "Y";
        }
        if(!(isPhotoReview.equals("Y") || isPhotoReview.equals("N"))){
            return new BaseResponse<>(GET_REVIEWS_INVALID_IS_PHOTO_REVEIW);
        }
        if(!(filter >= 1 && filter <= 4)){
            return new BaseResponse<>(GET_REVIEWS_INVALID_FILTER);
        }

        try {
            int userIdByJwt = jwtService.getUserId();
            if (reviewProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if (!isRegexInteger(storeId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int store_id = Integer.parseInt(storeId);
            GetReviewRes getReviewRes = reviewProvider.getReviewRes(store_id);
            List<Review> reviewList;

            // 포토리뷰
            if(isPhotoReview.equals("Y")){
                reviewList = reviewProvider.getReviewByPhoto(store_id, filter);
            }
            // 포토리뷰 X
            else{
                reviewList = reviewProvider.getReviews(store_id, filter);
            }
            // set isPhotoReview
            getReviewRes.setPhotoReviewYn(isPhotoReview);
            // set filter
            if(filter == 1){
                getReviewRes.setFilter("최신 순");
            } else if(filter == 2){
                getReviewRes.setFilter("리뷰 도움순");
            } else if(filter == 3){
                getReviewRes.setFilter("별점 높은 순");
            } else{
                getReviewRes.setFilter("별점 낮은 순");
            }
            // set reviewList
            getReviewRes.setReviewList(reviewList);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}