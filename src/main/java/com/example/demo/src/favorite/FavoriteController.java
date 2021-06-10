package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.favorite.model.GetFavoriteStoreRes;
import com.example.demo.src.favorite.model.PostFavoriteReq;
import com.example.demo.src.favorite.model.StoreInfo;
import com.example.demo.src.store.model.GCS;
import com.example.demo.src.store.model.GetStoreRes;
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
@RequestMapping("/app/favorites")
public class FavoriteController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteService favoriteService;
    private final FavoriteProvider favoriteProvider;
    private final JwtService jwtService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService, FavoriteProvider favoriteProvider, JwtService jwtService) {
        this.favoriteService = favoriteService;
        this.favoriteProvider = favoriteProvider;
        this.jwtService = jwtService;
    }

    /**
     * 즐겨찾기 활성/비활성 API
     * [POST] /favorites
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> setUpFavorite(@RequestBody PostFavoriteReq postFavoriteReq) throws BaseException {
        if(postFavoriteReq.getStoreId() == null){
            return new BaseResponse<>(POST_FAVORITES_EMPTY_STORE_ID);
        }
        if(favoriteProvider.checkStore(postFavoriteReq.getStoreId()) == 0){
            return new BaseResponse<>(INVALID_STORE_ID);
        }
        try{
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(favoriteProvider.checkUserStatusByUserId(userIdByJwt) == 0){
                return new BaseResponse<>(DELETED_USER);
            }

            if(favoriteProvider.checkFavorite(userIdByJwt, postFavoriteReq.getStoreId()) == 0){
                favoriteService.createFavorite(userIdByJwt, postFavoriteReq.getStoreId());
            } else{
                favoriteService.modifyFavoriteStatus(userIdByJwt, postFavoriteReq.getStoreId());
            }
            return new BaseResponse<>("success");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 즐겨찾기 매장 조회 API
     * [GET] /favorites
     *
     * 즐겨찾기 매장 조회 (필터) API
     * [GET] /favorites?filter=
     *
     * @return BaseResponse<GetFavoriteStoreRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetFavoriteStoreRes> getFavoriteStoreResBaseResponse(@RequestParam(required = false) String filter) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (favoriteProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(favoriteProvider.checkFavoriteByUserId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_FAVORITES);
            }

            GCS gcs = favoriteProvider.getGCS(userIdByJwt);
            BigDecimal userLatitude = gcs.getLatitude();
            BigDecimal userLongitude = gcs.getLongitude();

            String totalCount = null;
            List<StoreInfo> storeInfoList = null;

            if(filter == null){
                storeInfoList = favoriteProvider.getStoreInfos(userLatitude, userLongitude);
                totalCount = favoriteProvider.getTotalFavoriteCount(userIdByJwt);
                GetFavoriteStoreRes getFavoriteStoreRes = new GetFavoriteStoreRes(totalCount, "자주 주문한 순", storeInfoList);
                return new BaseResponse<>(getFavoriteStoreRes);
            }
            if(!isRegexInteger(filter)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int filterId = Integer.parseInt(filter);

            if(!(filterId >= 1 && filterId <= 3)){
                return new BaseResponse<>(POST_FAVORITES_INVALID_FILTER);
            }

            if(filterId == 1){
                storeInfoList = favoriteProvider.getStoreInfos(userLatitude, userLongitude);
                totalCount = favoriteProvider.getTotalFavoriteCount(userIdByJwt);
                GetFavoriteStoreRes getFavoriteStoreRes = new GetFavoriteStoreRes(totalCount, "자주 주문한 순", storeInfoList);
                return new BaseResponse<>(getFavoriteStoreRes);
            } else if(filterId == 2){
                storeInfoList = favoriteProvider.getStoreInfos(userLatitude, userLongitude);
                totalCount = favoriteProvider.getTotalFavoriteCount(userIdByJwt);
                GetFavoriteStoreRes getFavoriteStoreRes = new GetFavoriteStoreRes(totalCount, "최근 주문한 순", storeInfoList);
                return new BaseResponse<>(getFavoriteStoreRes);
            }
            storeInfoList = favoriteProvider.getStoreInfos(userLatitude, userLongitude);
            totalCount = favoriteProvider.getTotalFavoriteCount(userIdByJwt);
            GetFavoriteStoreRes getFavoriteStoreRes = new GetFavoriteStoreRes(totalCount, "최근 추가한 순", storeInfoList);
            return new BaseResponse<>(getFavoriteStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}