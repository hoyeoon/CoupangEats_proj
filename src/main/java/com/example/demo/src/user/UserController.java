package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DELETED_USER;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        String pwd = postUserReq.getPassword();
        String email = postUserReq.getEmail();
        String name = postUserReq.getName();
        String phone = postUserReq.getPhone();

        if(email == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(pwd == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(postUserReq.getPhone() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }

        // 정규표현
        if(!isRegexEmail(email)){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(userProvider.checkEmail(email) == 1){
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }
        if(!((isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && isRegexEngSpecialCharCombinationPwd(pwd)))
        ){
            return new BaseResponse<>(POST_USERS_PASSWORD_COMBINATION);
        }
        if(isRegexSamePwd(pwd) || isContinuousPwd(pwd)){
            return new BaseResponse<>(POST_USERS_PASSWORD_CONTINUOUS_OR_SAME_WORD);
        }
        if(isContainEmailId(pwd, email)){
            return new BaseResponse<>(POST_USERS_PASSWORD_CONTAIN_ID);
        }
        if(!isRegexName(name)){
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        if(!isRegexPhone(phone)){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        if(userProvider.checkPhone(phone) == 1){
            return new BaseResponse<>(POST_USERS_EXISTS_PHONE);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        if(postLoginReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postLoginReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(userProvider.checkEmail(postLoginReq.getEmail()) == 0){
            return new BaseResponse<>(FAILED_TO_LOGIN);
        }

        // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
        if(userProvider.checkUserStatusByEmail(postLoginReq.getEmail()) == 0){
            return new BaseResponse<>(DELETED_USER);
        }

        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
