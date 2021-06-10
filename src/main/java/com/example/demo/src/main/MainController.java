package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.main.model.GetAdvertiseImageRes;
import com.example.demo.src.main.model.GetEventImageRes;
import com.example.demo.src.main.model.GetStoreCategoryRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DELETED_USER;

@RestController
@RequestMapping("/app")
public class MainController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainService mainService;
    private final MainProvider mainProvider;
    private final JwtService jwtService;

    @Autowired
    public MainController(MainService mainService, MainProvider mainProvider, JwtService jwtService) {
        this.mainService = mainService;
        this.mainProvider = mainProvider;
        this.jwtService = jwtService;
    }

    /**
     * 메인 화면 - 주소 표시 API
     * [GET] /users/addresses
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/users/addresses")
    public BaseResponse<String> getMainAddress() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (mainProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            return new BaseResponse<>(mainProvider.getMainAddress(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메인 화면 - 이벤트 이미지 API
     * [GET] /event-images
     * @return BaseResponse<List<GetEventImageRes>>
     */
    @ResponseBody
    @GetMapping("/event-images")
    public BaseResponse<List<GetEventImageRes>> getEventImageRes() {
        try {
            return new BaseResponse<>(mainProvider.getEventImageRes());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메인 화면 - 매장 카테고리 API
     * [GET] /store-categories
     * @return BaseResponse<List<GetStoreCategoryRes>>
     */
    @ResponseBody
    @GetMapping("/store-categories")
    public BaseResponse<List<GetStoreCategoryRes>> getStoreCategoryRes() {
        try {
            return new BaseResponse<>(mainProvider.getStoreCategoryRes());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메인 화면 - 광고 이미지 API
     * [GET] /advertise-images
     * @return BaseResponse<GetAdvertiseImageRes>
     */
    @ResponseBody
    @GetMapping("/advertise-images")
    public BaseResponse<GetAdvertiseImageRes> getAdvertiseImageRes() {
        try {
            return new BaseResponse<>(mainProvider.getAdvertiseImageRes());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}