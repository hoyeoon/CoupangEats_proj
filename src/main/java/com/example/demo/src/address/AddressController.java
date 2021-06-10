package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/addresses")
public class AddressController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AddressService addressService;
    private final AddressProvider addressProvider;
    private final JwtService jwtService;

    @Autowired
    public AddressController(AddressService addressService, AddressProvider addressProvider, JwtService jwtService) {
        this.addressService = addressService;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;
    }

    /**
     * 배달지 주소 등록 API
     * [POST] /addresses
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> createAddress(@RequestBody PostAddressReq postAddressReq){
        if(postAddressReq.getAddress() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }
        if(postAddressReq.getDetailAddress() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_DETAIL_ADDRESS);
        }
        if(postAddressReq.getAddressType() == null){
            postAddressReq.setAddressType("E");
        }
        if(!postAddressReq.getAddressType().equals("E") && !postAddressReq.getAddressType().equals("H") && !postAddressReq.getAddressType().equals("C")){
            return new BaseResponse<>(POST_USERS_INVALID_TYPE);
        }
        try{
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(addressProvider.checkUserStatusByUserId(userIdByJwt) == 0){
                return new BaseResponse<>(DELETED_USER);
            }
            addressService.createUserAddress(postAddressReq, userIdByJwt);
            return new BaseResponse<>("배달 주소가 변경되었습니다.");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 배달지 주소 조회 API
     * [GET] /addresses
     * @return BaseResponse<List<GetAddressRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetAddressRes>> getAddresses() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (addressProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            List<GetAddressRes> addresses = addressProvider.getAddresses(userIdByJwt);
            return new BaseResponse<>(addresses);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 배달지 주소 변경 API
     * [PATCH] /addresses/:addressId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping({"/{addressId}", ""})
    public BaseResponse<String> modifyAddressActiveYn(@PathVariable(required = false) String addressId){
        if(addressId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();
            if (addressProvider.checkUserStatusByUserId(userIdByJwt) == 0) {
                return new BaseResponse<>(DELETED_USER);
            }
            if(!isRegexInteger(addressId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }

            int id = Integer.parseInt(addressId);
            if(addressProvider.checkAddressId(userIdByJwt, id) == 0){
                return new BaseResponse<>(INVALID_ADDRESS_ID);
            }

            addressService.modifyAddressActiveYn(userIdByJwt, id);
            return new BaseResponse<>("배달 주소가 변경되었습니다.");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}