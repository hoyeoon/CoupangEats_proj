package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreRes> getStores(BigDecimal userLatitude, BigDecimal userLongitude) {
        String getStoresQuery = "select s.id as storeId, storeName, newStoreYn, cheetahDeliveryYn, concat(deliveryTime,'-',deliveryTime+10,'분') as deliveryTime,\n" +
                "                concat(round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1), 'km') AS distance,\n" +
                "                case when min(deliveryFee) = 0 then '무료배달' else concat('배달비 ', format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "                from Store s, DeliveryFeePolicy p\n" +
                "                where s.id = p.storeId and round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1) < 10\n" +
                "                group by storeId;";
        Object[] getStoresParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude};
        return this.jdbcTemplate.query(getStoresQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("newStoreYn"),
                        rs.getString("cheetahDeliveryYn"),
                        rs.getString("deliveryTime"),
                        rs.getString("distance"),
                        rs.getString("deliveryFee")
                        ),
                getStoresParams);
    }

    public List<GetStoreRes> getStoresByCategoryId(BigDecimal userLatitude, BigDecimal userLongitude, int categoryId) {
        String getStoresByCategoryIdQuery = "select s.id as storeId, storeName, newStoreYn, cheetahDeliveryYn, concat(deliveryTime,'-',deliveryTime+10,'분') as deliveryTime,\n" +
                "                concat(round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1), 'km') AS distance,\n" +
                "                case when min(deliveryFee) = 0 then '무료배달' else concat('배달비 ', format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "                from Store s, DeliveryFeePolicy p\n" +
                "                where s.id = p.storeId and round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1) < 10 and (select exists(select * from StoreCategoryInfo si where si.storeCategoryId = ? and si.storeId = s.id) = 1)\n" +
                "                group by storeId;";
        Object[] getStoresByCategoryIdParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude, categoryId};
        return this.jdbcTemplate.query(getStoresByCategoryIdQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("newStoreYn"),
                        rs.getString("cheetahDeliveryYn"),
                        rs.getString("deliveryTime"),
                        rs.getString("distance"),
                        rs.getString("deliveryFee")
                ),
                getStoresByCategoryIdParams);
    }

    public List<GetStoreRes> getStoresByKeyword(BigDecimal userLatitude, BigDecimal userLongitude, String storeName) {
        String getStoresQuery = "select s.id as storeId, storeName, newStoreYn, cheetahDeliveryYn, concat(deliveryTime,'-',deliveryTime+10,'분') as deliveryTime,\n" +
                "                concat(round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1), 'km') AS distance,\n" +
                "                case when min(deliveryFee) = 0 then '무료배달' else concat('배달비 ', format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "                from Store s, DeliveryFeePolicy p\n" +
                "                where s.id = p.storeId and round((6371*acos(\n" +
                "                cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "                +sin(radians(?))*sin(radians(latitude)))), 1) < 10 " +
                "                and s.storeName like concat('%', ?, '%')\n" +
                "                group by storeId;";
        Object[] getStoresParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude, storeName};
        return this.jdbcTemplate.query(getStoresQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("newStoreYn"),
                        rs.getString("cheetahDeliveryYn"),
                        rs.getString("deliveryTime"),
                        rs.getString("distance"),
                        rs.getString("deliveryFee")
                ),
                getStoresParams);
    }

    public GCS getGCS(int userId){
        String getGCSQuery = "select latitude, longitude from UserAddress where userId = ? and activeYn = 'Y'";
        int getGCSParams = userId;
        return this.jdbcTemplate.queryForObject(getGCSQuery,
                (rs, rowNum) -> new GCS(
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")),
                getGCSParams);
    }

    public int checkUserStatusByUserId(int userId) {
        String checkUserStatusByUserIdQuery = "select exists(select * from User where id = ? and status = 'N')";
        int checkUserStatusByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserStatusByUserIdQuery, int.class, checkUserStatusByUserIdParams);
    }

    public List<GetStoreImageRes> getStoreImages(int storeId){
        String getStoreImagesQuery = "select storeImageUrl from StoreImage where storeId = ? and status = 'N'";
        int getStoreImagesParams = storeId;
        return this.jdbcTemplate.query(getStoreImagesQuery,
                (rs, rowNum) -> new GetStoreImageRes(
                        rs.getString("storeImageUrl")),
        getStoreImagesParams);
    }

    public String getReviewScoreAndCount(int storeId){
        String getReviewScoreAndCountQuery = "select concat(round(avg(score), 1), '(', count(*), ')') as reviewScoreAndCount from Review where storeId = ? and status = 'N'";
        int getReviewScoreAndCountParams = storeId;
        return this.jdbcTemplate.queryForObject(getReviewScoreAndCountQuery, String.class, getReviewScoreAndCountParams);
    }

    public GetStoreInfosRes getStoreInfos(int storeId) {
        String getStoreInfoQuery = "select s.id as storeId, storeName, round(avg(r.score), 1) as totalReviewScore, concat('리뷰 ', count(r.score), '개') as totalReviewCount,\n" +
                "concat(s.deliveryTime, '-', s.deliveryTime + 10, '분') as deliveryTime, s.newStoreYn, s.cheetahDeliveryYn\n" +
                "from Store s, Review r\n" +
                "where s.id = r.storeId and  r.status = 'N' and s.status = 'N' and s.id = ?";
        int getStoreInfoParams = storeId;
        return this.jdbcTemplate.queryForObject(getStoreInfoQuery,
                (rs, rowNum) -> new GetStoreInfosRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getDouble("totalReviewScore"),
                        rs.getString("totalReviewCount"),
                        rs.getString("deliveryTime"),
                        rs.getString("newStoreYn"),
                        rs.getString("cheetahDeliveryYn")
                ),
                getStoreInfoParams);
    }

    public String getDeliveryFee(int storeId){
        String getDeliveryFeeQuery = "select case when min(deliveryFee) = 0 then '무료배달' else concat(format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "from Store s, DeliveryFeePolicy p\n" +
                "where p.storeId = s.id and  p.status = 'N' and s.status = 'N' and s.id = ?";
        int getDeliveryFeeParams = storeId;
        return this.jdbcTemplate.queryForObject(getDeliveryFeeQuery, String.class, getDeliveryFeeParams);
    }

    public String getMinOrderPrice(int storeId){
        String getMinOrderPriceQuery = "select concat(format(min(minOrderPrice), 0), '원') as minOrderPrice\n" +
                "from Store s, DeliveryFeePolicy p\n" +
                "where p.storeId = s.id and  p.status = 'N' and s.status = 'N' and s.id = ?";
        int getMinOrderPriceParams = storeId;
        return this.jdbcTemplate.queryForObject(getMinOrderPriceQuery, String.class, getMinOrderPriceParams);
    }

    public List<Review> getStoreReviews(int storeId) {
        String getStoreReviewsQuery = "select r.id as reviewId, i.reviewImageUrl, r.description as reviewDescription, r.score as reviewScore\n" +
                "from Review r, ReviewImage i\n" +
                "where r.status = 'N' and i.status = 'N' and r.id = i.reviewId and r.storeId = ?";
        int getStoreReviewsParams = storeId;
        return this.jdbcTemplate.query(getStoreReviewsQuery,
                (rs, rowNum) -> new Review(
                        rs.getInt("reviewId"),
                        rs.getString("reviewImageUrl"),
                        rs.getString("reviewDescription"),
                        rs.getInt("reviewScore")
                ),
                getStoreReviewsParams);
    }

    public List<MenuCategory> getMenuCategories(int storeId) {
        String getMenuCategoriesQuery = "select id as menuCategoryId, name as menuCategoryName\n" +
                "from MenuCategory c\n" +
                "where c.status = 'N' and c.storeId = ?";
        int getMenuCategoriesParams = storeId;
        return this.jdbcTemplate.query(getMenuCategoriesQuery,
                (rs, rowNum) -> new MenuCategory(
                        rs.getInt("menuCategoryId"),
                        rs.getString("menuCategoryName")
                ),
                getMenuCategoriesParams);
    }

    public List<Menu> getMenus(int menuCategoryId) {
        String getMenusQuery = "select m.id as menuId, m.manyOrdersYn, m.reviewBestYn, m.name as menuName, " +
                "concat(format(m.price, 0), '원') as menuPrice, m.description as menuDescription, menuImageUrl\n" +
                "from MenuCategory c, Menu m\n" +
                "where c.id = m.menuCategoryId and c.status = 'N' and m.status = 'N' and m.menuCategoryId = ?";
        int getMenusParams = menuCategoryId;
        return this.jdbcTemplate.query(getMenusQuery,
                (rs, rowNum) -> new Menu(
                        rs.getInt("menuId"),
                        rs.getString("manyOrdersYn"),
                        rs.getString("reviewBestYn"),
                        rs.getString("menuName"),
                        rs.getString("menuPrice"),
                        rs.getString("menuDescription"),
                        rs.getString("menuImageUrl")
                ),
                getMenusParams);
    }

    public List<GetNewStoreRes> getNewStores(BigDecimal userLatitude, BigDecimal userLongitude) {
        String getNewStoresQuery = "select s.id as storeId, concat(deliveryTime,'-',deliveryTime+10,'분') as deliveryTime, storeName,\n" +
                "concat(round((6371*acos(cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))+sin(radians(?))*sin(radians(latitude)))), 1), 'km') AS distance,\n" +
                "case when min(deliveryFee) = 0 then '무료배달' else concat('배달비 ', format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "from Store s, DeliveryFeePolicy p\n" +
                "where s.id = p.storeId " +
                "and round((6371*acos(cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))+sin(radians(?))*sin(radians(latitude)))), 1) < 10 " +
                "and s.newStoreYn = 'Y'\n" +
                "group by storeId";
        Object[] getNewStoresParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude};
        return this.jdbcTemplate.query(getNewStoresQuery,
                (rs, rowNum) -> new GetNewStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("deliveryTime"),
                        rs.getString("storeName"),
                        rs.getString("distance"),
                        rs.getString("deliveryFee")
                ),
                getNewStoresParams);
    }

    public List<GetNewStoreRes> getNewStoresByCategoryId(BigDecimal userLatitude, BigDecimal userLongitude, int categoryId) {
        String getNewStoresByCategoryIdQuery = "select s.id as storeId, concat(deliveryTime,'-',deliveryTime+10,'분') as deliveryTime, storeName,\n" +
                "concat(round((6371*acos(cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))+sin(radians(?))*sin(radians(latitude)))), 1), 'km') AS distance,\n" +
                "case when min(deliveryFee) = 0 then '무료배달' else concat('배달비 ', format(min(deliveryFee), 0), '원') end as deliveryFee\n" +
                "from Store s, DeliveryFeePolicy p\n" +
                "where s.id = p.storeId " +
                "and round((6371*acos(cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))+sin(radians(?))*sin(radians(latitude)))), 1) < 10 " +
                "and s.newStoreYn = 'Y' and (select exists(select * from StoreCategoryInfo si where si.storeCategoryId = ? and si.storeId = s.id) = 1)\n" +
                "group by storeId";
        Object[] getNewStoresByCategoryIdParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude, categoryId};
        return this.jdbcTemplate.query(getNewStoresByCategoryIdQuery,
                (rs, rowNum) -> new GetNewStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("deliveryTime"),
                        rs.getString("storeName"),
                        rs.getString("distance"),
                        rs.getString("deliveryFee")
                ),
                getNewStoresByCategoryIdParams);
    }

    public String getStoreImageUrl(int storeId){
        String getStoreImageUrlQuery = "select storeImageUrl from StoreImage where storeId = ? and status = 'N' limit 1";
        int getStoreImageUrlParams = storeId;
        return this.jdbcTemplate.queryForObject(getStoreImageUrlQuery, String.class, getStoreImageUrlParams);
    }

    public int checkStoreId(int storeId) {
        String checkStoreIdQuery = "select(exists(select * from Store where status = 'N' and id = ?))";
        int checkStoreIdParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreIdQuery, int.class, checkStoreIdParams);
    }

    public GetStoreDetailInfoRes getStoreDetailInfoRes(int storeId){
        String getStoreDetailInfoResQuery = "select id as storeId, storeName, concat('전화번호: ', phone) as phone, concat('주소: ', address) as address,\n" +
                "       concat('대표자명: ', ownerName) as ownerName, concat('사업자등록번호: ', businessLicenseNo) as businessLicenseNo,\n" +
                "       concat('상호명: ', businessName) as businessName, operatingTime, introduction, countryOfOrigin\n" +
                "from Store where id = ? and status = 'N'";
        int getStoreDetailInfoResParams = storeId;
        return this.jdbcTemplate.queryForObject(getStoreDetailInfoResQuery,
                (rs, rowNum) -> new GetStoreDetailInfoRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("ownerName"),
                        rs.getString("businessLicenseNo"),
                        rs.getString("businessName"),
                        rs.getString("operatingTime"),
                        rs.getString("introduction"),
                        rs.getString("countryOfOrigin")
                ),
        getStoreDetailInfoResParams
        );
    }

    public String getStoreNotice(int storeId){
        String checkStoreNoticeQuery = "select ifnull(notice, '') from Store where status = 'N' and id = ?";
        int checkStoreNoticeParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreNoticeQuery, String.class, checkStoreNoticeParams);
    }

    public List<GetDeliveryPolicyInfoRes> getDeliveryPolicyInfoRes(int storeId){
        String getDeliveryPolicyInfoResQuery = "select case when maxOrderPrice is not null then concat(concat(format(minOrderPrice, 0), '원'), ' ~ ' ,concat(format(maxOrderPrice, 0), '원'))\n" +
                "    else concat(concat(format(minOrderPrice, 0), '원'), ' ~') end as orderPrice,\n" +
                "       case when deliveryFee = 0 then '무료' else concat(format(deliveryFee, 0), '원') end as deliveryFee\n" +
                "from DeliveryFeePolicy p\n" +
                "where storeId = ? and status ='N'";
        int getDeliveryPolicyInfoResParams = storeId;
        return this.jdbcTemplate.query(getDeliveryPolicyInfoResQuery,
                (rs, rowNum) -> new GetDeliveryPolicyInfoRes(
                        rs.getString("orderPrice"),
                        rs.getString("deliveryFee")),
                getDeliveryPolicyInfoResParams
        );
    }
}