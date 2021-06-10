package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/stores")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreService storeService;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;

    @Autowired
    public StoreController(StoreService storeService, StoreProvider storeProvider, JwtService jwtService) {
        this.storeService = storeService;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;
    }

    /**
     * 매장 선택 화면 - 기본 API
     * [GET] /stores
     *
     * 매장 선택 화면 - 기본 (카테고리 별) API
     * [GET] /stores?categoryId=
     *
     * @return BaseResponse<List<GetStoreRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetStoreRes>> getStores(@RequestParam(required = false) String categoryId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (storeProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            GCS gcs = storeProvider.getGCS(userIdByJwt);
            BigDecimal userLatitude = gcs.getLatitude();
            BigDecimal userLongitude = gcs.getLongitude();

            if(categoryId == null){
                return new BaseResponse<>(storeProvider.getStores(userLatitude, userLongitude));
            }
            if(!isRegexInteger(categoryId)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int id = Integer.parseInt(categoryId);
            List<GetStoreRes> stores = storeProvider.getStoresByCategoryId(userLatitude, userLongitude, id);
            return new BaseResponse<>(stores);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 매장 선택 화면 - 신규 API
     * [GET] /stores/new
     *
     * 매장 선택 화면 - 신규 (카테고리 별) API
     * [GET] /stores/new?categoryId=
     *
     * @return BaseResponse<List<GetNewStoreRes>>
     */
    @ResponseBody
    @GetMapping("/new")
    public BaseResponse<List<GetNewStoreRes>> getNewStores(@RequestParam(required = false) String categoryId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (storeProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            GCS gcs = storeProvider.getGCS(userIdByJwt);
            BigDecimal userLatitude = gcs.getLatitude();
            BigDecimal userLongitude = gcs.getLongitude();

            if(categoryId == null){
                return new BaseResponse<>(storeProvider.getNewStores(userLatitude, userLongitude));
            }
            if(!isRegexInteger(categoryId)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int id = Integer.parseInt(categoryId);
            List<GetNewStoreRes> newStores = storeProvider.getNewStoresByCategoryId(userLatitude, userLongitude, id);
            return new BaseResponse<>(newStores);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 이미지 API
     * [GET] /stores/:storeId/images
     * @return BaseResponse<List<StoreImage>>
     */
    @ResponseBody
    @GetMapping("/{storeId}/images")
    public BaseResponse<List<GetStoreImageRes>> getStoreImages(@PathVariable(required = false) String storeId) {
        if(storeId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            return new BaseResponse<>(storeProvider.getStoreImages(id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 매장 정보 API
     * [GET] /stores/:storeId/infos
     * @return BaseResponse<GetStoreInfosRes>
     */
    @ResponseBody
    @GetMapping("/{storeId}/infos")
    public BaseResponse<GetStoreInfosRes> getStoreInfo(@PathVariable(required = false) String storeId) {
        if(storeId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            GetStoreInfosRes getStoreInfosRes = storeProvider.getStoreInfos(id);
            return new BaseResponse<>(getStoreInfosRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 매장 리뷰 API
     * [GET] /stores/:storeId/reviews
     * @return BaseResponse<GetStoreReviewRes>
     */
    @ResponseBody
    @GetMapping("/{storeId}/reviews")
    public BaseResponse<GetStoreReviewRes> getStoreReviews(@PathVariable(required = false) String storeId) {
        if(storeId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            GetStoreReviewRes getStoreReviewRes = new GetStoreReviewRes(id);
            getStoreReviewRes.setReviewList(storeProvider.getStoreReviews(id));
            return new BaseResponse<>(getStoreReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 메뉴 목록 API
     * [GET] /stores/:storeId/menus
     * @return BaseResponse<GetStoreMenusRes>
     */
    @ResponseBody
    @GetMapping("/{storeId}/menus")
    public BaseResponse<GetStoreMenusRes> getStoreMenus(@PathVariable(required = false) String storeId) {
        if(storeId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            GetStoreMenusRes getStoreMenusRes = new GetStoreMenusRes(id);
            getStoreMenusRes.setMenuCategoryList(storeProvider.getMenuCategories(id));
            return new BaseResponse<>(getStoreMenusRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 매장 / 원산지 정보 API
     * [GET] /app/stores/:storeId/detail-infos
     * @return BaseResponse<GetStoreDetailInfoRes>
     */
    @ResponseBody
    @GetMapping("/{storeId}/detail-infos")
    public BaseResponse<GetStoreDetailInfoRes> getStoreDetailInfoResBaseResponse(@PathVariable(required = false) String storeId) {
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            return new BaseResponse<>(storeProvider.getStoreDetailInfoRes(id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 매장 화면 - 배달비 안내 API
     * [GET] /app/stores/:storeId/delivery-policy
     * @return BaseResponse<List<GetDeliveryPolicyInfoRes>>
     */
    @ResponseBody
    @GetMapping("/{storeId}/delivery-policy")
    public BaseResponse<List<GetDeliveryPolicyInfoRes>> getDeliveryPolicyInfoResBaseResponse(@PathVariable(required = false) String storeId) {
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);
            if(storeProvider.checkStoreId(id) == 0){
                return new BaseResponse<>(INVALID_STORE_ID);
            }
            return new BaseResponse<>(storeProvider.getDeliveryPolicyInfoRes(id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 매장 검색하기 API
     * [GET] /stores/search?keyword=
     *
     * @return BaseResponse<List<GetStoreRes>>
     */
    @ResponseBody
    @GetMapping("search")
    public BaseResponse<List<GetStoreRes>> getStoresByKeyword(@RequestParam(required = false) String keyword) {
        if(keyword == null){
            return new BaseResponse<>(EMPTY_QUERY_PARAMS);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (storeProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            GCS gcs = storeProvider.getGCS(userIdByJwt);
            BigDecimal userLatitude = gcs.getLatitude();
            BigDecimal userLongitude = gcs.getLongitude();

            List<GetStoreRes> stores = storeProvider.getStoresByKeyword(userLatitude, userLongitude, keyword);
            return new BaseResponse<>(stores);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
