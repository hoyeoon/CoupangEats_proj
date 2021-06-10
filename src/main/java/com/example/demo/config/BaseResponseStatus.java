package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    EMPTY_PATH_VARIABLE(false, 2004, "PathVariable를 입력해주세요."),
    INVALID_ADDRESS(false, 2005, "올바르지 않은 주소입니다."),
    INVAILD_PATH_VARIABLE(false, 2006, "올바르지 않은 PathVariable 형식입니다."),
    INVAILD_QUERY_PARAMS(false, 2007, "올바르지 않은 Query Params 형식입니다."),
    EMPTY_QUERY_PARAMS(false, 2008, "Query Params를 입력해주세요."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일을 올바르게 입력해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"이미 가입된 이메일 주소입니다. 다른 이메일을 입력하여 주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2018, "비밀번호를 입력해주세요."),
    POST_USERS_PASSWORD_COMBINATION(false, 2019, "영문/숫자/특수문자 2가지 이상 조합(8~20자)"),
    POST_USERS_PASSWORD_CONTINUOUS_OR_SAME_WORD(false, 2020, "3개 이상 연속되거나 동일한 문자/숫자 제외"),
    POST_USERS_PASSWORD_CONTAIN_ID(false, 2021, "아이디(이메일 제외)"),
    POST_USERS_EMPTY_NAME(false, 2022, "이름을 입력해주세요."),
    POST_USERS_INVALID_NAME(false, 2023, "이름을 정확히 입력하세요."),
    POST_USERS_EMPTY_PHONE(false, 2024, "휴대폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONE(false, 2025, "휴대폰 번호 형식을 확인해주세요."),
    POST_USERS_EXISTS_PHONE(false, 2026, "아이디(이메일)로 가입된 휴대폰 번호입니다."),

    // [POST] /addresses
    POST_USERS_EMPTY_ADDRESS(false, 2030, "주소를 입력하세요."),
    POST_USERS_EMPTY_DETAIL_ADDRESS(false, 2031, "상세 주소를 입력하세요."),
    POST_USERS_INVALID_TYPE(false, 2032, "주소 타입이 올바르지 않습니다. E : 기타, C : 회사, H : 집"),

    // [POST] /carts
    POST_CARTS_EMPTY_MENU_ID(false, 2040, "menuId를 입력하세요."),
    POST_CARTS_EMPTY_COUNT(false, 2041, "count를 입력하세요."),
    POST_CARTS_INVALID_MENU_ID(false, 2042, "1이상의 menuId를 입력하세요."),
    POST_CARTS_INVALID_COUNT(false, 2043, "1이상의 count를 입력하세요."),
    POST_CARTS_DIFFERENT_STORE(false, 2044, "같은 가게의 메뉴만 담을 수 있습니다."),

    // [DELETE] /carts/:cartMenuId
    DELETE_CARTS_INVALID_CART_MENU_ID(false, 2045, "1이상의 cartMenuId를 입력하세요."),

    // [GET] /carts/payments/:accountId
    GET_CARTS_INVALID_ACCOUNT_ID(false, 2046, "1이상의 accountId를 입력하세요."),

    // [POST] /orders
    POST_ORDERS_EMPTY_ACCOUNT_ID(false, 2050, "accountId를 입력하세요."),
    POST_ORDERS_EMPTY_TO_OWNER_MESSAGE(false, 2051, "toOwnerMessage를 입력하세요."),
    POST_ORDERS_EMPTY_RECYCLED_PRODUCT_YN(false, 2052, "recycledProductYn을 입력하세요."),
    POST_ORDERS_EMPTY_TO_RIDER_MESSAGE(false, 2053, "toRiderMessage를 입력하세요."),
    POST_ORDERS_INVALID_RECYCLED_PRODUCT_YN(false, 2054, "recycledProductYn은 Y 또는 N 값이 입력되어야 합니다."),

    // [POST] /favorites
    POST_FAVORITES_EMPTY_STORE_ID(false, 2060, "storeId를 입력하세요."),
    POST_FAVORITES_INVALID_FILTER(false, 2061, "1에서 3사이의 필터 값을 입력하세요."),

    // [POST] /reviews
    POST_REVIEWS_EMPTY_ORDER_INFO_ID(false, 2070, "orderInfoId를 입력하세요."),
    POST_REVIEWS_EMPTY_DESCRIPTION(false, 2071, "reviewDescription을 입력하세요."),
    POST_REVIEWS_INVALID_SCORE(false, 2072, "1이상 5이하의 reviewScore를 입력하세요."),

    // [GET] /reviews/:storeId
    GET_REVIEWS_INVALID_IS_PHOTO_REVEIW(false, 2080, "isPhotoReview는 Y 또는 N 값이 입력되어야 합니다."),
    GET_REVIEWS_INVALID_FILTER(false, 2081, "1에서 4사이의 필터 값을 입력하세요."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),
    DELETED_USER(false, 3015, "탈퇴한 유저입니다."),

    // [PATCH] /addresses/:addressId
    INVALID_ADDRESS_ID(false, 3020, "올바르지 않은 addressId 입니다."),

    // [GET] /stores/:storeId
    INVALID_STORE_ID(false, 3021, "올바르지 않은 storeId 입니다."),

    // [GET] [PATCH] /carts/:menuId
    INVALID_MENU_ID(false, 3022, "올바르지 않은 menuId 입니다."),

    // [GET] /menus/:menuId/price?count=
    INVALID_MENU_COUNT(false, 3023, "올바르지 않은 count 입니다."),

    // [DELETE] /carts/:cartMenuId
    INVALID_CART_MENU_ID(false, 3024, "올바르지 않은 cartMenuId 입니다."),

    // [POST] /orders
    INSUFFICIENT_BALANCE(false, 3025, "잔액이 부족합니다."),

    INVALID_COUPON_ID(false, 3026, "사용 불가능한 쿠폰입니다."),
    NOT_EXISTS_ACCOUNT_ID(false, 3027, "존재하지 않는 계좌입니다."),
    EMPTY_CARTS(false, 3028, "장바구니가 비어있습니다."),

    // [POST] /favorites
    EMPTY_FAVORITES(false, 3029, "즐겨찾는 맛집이 없습니다. 좋아하는 맛집에 ♥를 눌러주세요."),

    // [POST] /reviews
    INVALID_ORDER_INFO_ID(false, 3030, "올바르지 않은 orderInfoId 입니다."),
    EXISTS_REVIEW(false, 3031, "이미 리뷰가 존재합니다."),
    INVALID_ORDER_INFO_MENU_ID(false, 3032, "올바르지 않은 orderInfoMenuId 입니다."),

    // [GET] /users/reviews/:reviewId
    INVALID_REVIEW_ID(false, 3040, "올바르지 않은 reviewId 입니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
