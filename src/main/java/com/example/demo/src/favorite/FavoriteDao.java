package com.example.demo.src.favorite;

import com.example.demo.src.favorite.model.StoreInfo;
import com.example.demo.src.store.model.GCS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class FavoriteDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkUserStatusByUserId(int userId) {
        String checkUserStatusByUserIdQuery = "select exists(select * from User where id = ? and status = 'N')";
        int checkUserStatusByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserStatusByUserIdQuery, int.class, checkUserStatusByUserIdParams);
    }

    public int checkStore(int storeId){
        String checkStoreQuery = "select(exists(select * from Store where status = 'N' and id = ?))";
        int checkStoreParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreQuery, int.class, checkStoreParams);
    }

    public int checkFavorite(int userId, int storeId){
        String checkFavoriteQuery = "select(exists(select * from Favorites where userId = ? and storeId = ?))";
        Object[] checkFavoriteParams = new Object[]{userId, storeId};
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery, int.class, checkFavoriteParams);
    }

    public int checkFavoriteByUserId(int userId){
        String checkFavoriteByUserIdQuery = "select(exists(select * from Favorites where userId = ?))";
        int checkFavoriteByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkFavoriteByUserIdQuery, int.class, checkFavoriteByUserIdParams);
    }

    public void createFavorite(int userId, int storeId){
        String createFavoriteQuery = "insert into Favorites(userId, storeId) values(?, ?)";
        Object[] createFavoriteParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(createFavoriteQuery, createFavoriteParams);
    }

    public String checkFavoriteActiveStatus(int userId, int storeId){
        String checkFavoriteStatusQuery = "select activeYn from Favorites where userId = ? and storeId = ?";
        Object[] checkFavoriteStatusParams = new Object[]{userId, storeId};
        return this.jdbcTemplate.queryForObject(checkFavoriteStatusQuery, String.class, checkFavoriteStatusParams);
    }

    public void modifyFavoriteActiveStatusY(int userId, int storeId){
        String modifyFavoriteStatusYQuery = "update Favorites set activeYn = 'Y' where userId = ? and storeId = ?";
        Object[] modifyFavoriteStatusYParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(modifyFavoriteStatusYQuery, modifyFavoriteStatusYParams);
    }

    public void modifyFavoriteActiveStatusN(int userId, int storeId){
        String modifyFavoriteStatusNQuery = "update Favorites set activeYn = 'N' where userId = ? and storeId = ?";
        Object[] modifyFavoriteStatusNParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(modifyFavoriteStatusNQuery, modifyFavoriteStatusNParams);
    }

    public List<StoreInfo> storeInfoList(BigDecimal userLatitude, BigDecimal userLongitude){
        String storeInfoListQuery = "select s.id as storeId, i.storeImageUrl, s.storeName, s.cheetahDeliveryYn,\n" +
                "       concat(round((6371*acos(\n" +
                "        cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "        +sin(radians(?))*sin(radians(latitude)))), 1), 'km') as distance,\n" +
                "       concat(s.deliveryTime, '-', s.deliveryTime + 10, '분') as deliveryTime,\n" +
                "       case when p.deliveryFee = 0 then '무료배달' else concat('배달비 ', format(deliveryFee, 0), '원') end as deliveryFee\n" +
                "from Favorites f, Store s, StoreImage i, DeliveryFeePolicy p\n" +
                "where s.id = p.storeId and i.storeId = s.id and f.storeId = s.id and f.activeYn = 'Y'\n" +
                "  and i.status = 'N' and f.status = 'N' and s.status = 'N' and p.status = 'N'and\n" +
                "      round((6371*acos(\n" +
                "        cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))\n" +
                "       +sin(radians(?))*sin(radians(latitude)))), 1) < 10\n" +
                "group by s.createdAt order by s.createdAt desc";
        Object[] storeInfoListParams = new Object[]{userLatitude, userLongitude, userLatitude, userLatitude, userLongitude, userLatitude};
        return this.jdbcTemplate.query(storeInfoListQuery,
                (rs, rowNum) -> new StoreInfo(
                        rs.getInt("storeId"),
                        rs.getString("storeImageUrl"),
                        rs.getString("storeName"),
                        rs.getString("cheetahDeliveryYn"),
                        rs.getString("distance"),
                        rs.getString("deliveryTime"),
                        rs.getString("deliveryFee")),
                storeInfoListParams
        );
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

    public String getTotalFavoriteCount(int userId){
        String getTotalFavoriteCountQuery = "select concat('총 ', count(*), '개') from Favorites where activeYn = 'Y' and status = 'N' and userId = ?";
        int getTotalFavoriteCountParams = userId;
        return this.jdbcTemplate.queryForObject(getTotalFavoriteCountQuery, String.class, getTotalFavoriteCountParams);
    }

    public String getReviewScoreAndCount(int storeId){
        String getReviewScoreAndCountQuery = "select concat(round(avg(score), 1), '(', count(*), ')') as reviewScoreAndCount from Review where storeId = ? and status = 'N'";
        int getReviewScoreAndCountParams = storeId;
        return this.jdbcTemplate.queryForObject(getReviewScoreAndCountQuery, String.class, getReviewScoreAndCountParams);
    }
}