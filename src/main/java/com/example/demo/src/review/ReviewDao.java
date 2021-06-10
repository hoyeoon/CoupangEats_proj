package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
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

    public int checkOrderInfoId(int userId, int orderInfoId){
        String checkOrderInfoIdQuery = "select(exists(select * from OrderInfo where userId = ? and id = ?))";
        Object[] checkOrderInfoIdParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(checkOrderInfoIdQuery, int.class, checkOrderInfoIdParams);
    }

    public int checkExistReview(int userId, int orderInfoId){
        String checkExistReviewQuery = "select(exists(select * from Review where userId = ? and orderInfoId = ? and status = 'N'))";
        Object[] checkExistReviewParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(checkExistReviewQuery, int.class, checkExistReviewParams);
    }

    public int getStoreId(int userId, int orderInfoId){
        String getStoreIdQuery = "select storeId from OrderInfo where userId = ? and id = ?";
        Object[] getStoreIdParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(getStoreIdQuery, int.class, getStoreIdParams);
    }

    public int createReview(int storeId, int userId, PostReviewReq postReviewReq){
        String createReviewQuery = "insert into Review(storeId, orderInfoId, userId, score, description) values(?,?,?,?,?)";
        Object[] createReviewParams = new Object[]{storeId, postReviewReq.getOrderInfoId(), userId, postReviewReq.getReviewScore(), postReviewReq.getReviewDescription()};
        this.jdbcTemplate.update(createReviewQuery, createReviewParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void createReviewImage(int reviewId, String reviewImageUrl){
        String createReviewImageQuery = "insert into ReviewImage(reviewId, reviewImageUrl) values(?,?)";
        Object[] createReviewImageParams = new Object[]{reviewId, reviewImageUrl};
        this.jdbcTemplate.update(createReviewImageQuery, createReviewImageParams);
    }

    public int checkOrderMenuId(int orderMenuId, PostReviewReq postReviewReq){
        String checkOrderMenuIdQuery = "select case when ? in (select menuId from OrderPriceInfo where orderInfoId = ? and status = 'N')  then 1 else 0 end";
        Object[] checkOrderMenuIdParams = new Object[]{orderMenuId, postReviewReq.getOrderInfoId()};
        return this.jdbcTemplate.queryForObject(checkOrderMenuIdQuery, int.class, checkOrderMenuIdParams);
    }

    public int checkReviewId(int userId, int reviewId){
        String checkReviewIdQuery = "select(exists(select * from Review where userId = ? and id = ? and status = 'N'))";
        Object[] checkReviewIdParams = new Object[]{userId, reviewId};
        return this.jdbcTemplate.queryForObject(checkReviewIdQuery, int.class, checkReviewIdParams);
    }

    public GetUserReviewRes getUserReviewRes(int userId, int reviewId){
        String getUserReviewResQuery = "select storeId, storeName, score,\n" +
                "case when date_format(CURDATE(), '%Y') > date_format(r.updatedAt, '%Y') then '이전 해' else\n" +
                "case when date_format(CURDATE(), '%m') > date_format(r.updatedAt, '%m') then '지난 달' else\n" +
                "case when date_format(CURDATE(), '%d') = date_format(r.updatedAt, '%d') then '오늘' else\n" +
                "case when date_format(CURDATE(), '%d') - 1 = date_format(r.updatedAt, '%d') then '어제' else\n" +
                "case when date_format(CURDATE(), '%d') - 2 = date_format(r.updatedAt, '%d') then '그제' else\n" +
                "case when date_format(ADDDATE(CURDATE(), - WEEKDAY(CURDATE()) + 0 ), '%d') < date_format(r.updatedAt, '%d') and\n" +
                "date_format(r.updatedAt, '%d') < date_format(ADDDATE( CURDATE(), - WEEKDAY(CURDATE()) + 6 ), '%d') then '이번 주' else '이번 달'\n" +
                "end end end end end end as updatedAt, r.description,\n" +
                "concat(helpCount, '명에게 도움이 되었습니다') as helpCount,\n" +
                "case when 10 - DATEDIFF(CURDATE(), r.createdAt) > 0 then concat('리뷰 수정기간이 ',  10 - DATEDIFF(CURDATE(), r.createdAt), '일 남았습니다.')\n" +
                "    else '리뷰 수정기간이 지났습니다.' end as remainingUpdateDate\n" +
                "from Review r, Store s\n" +
                "where r.storeId = s.id and r.status = 'N' and r.userId = ? and r.id = ?";
        Object[] getUserReviewResParams = new Object[]{userId, reviewId};
        return this.jdbcTemplate.queryForObject(getUserReviewResQuery,
                (rs, rowNum) -> new GetUserReviewRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getInt("score"),
                        rs.getString("updatedAt"),
                        rs.getString("description"),
                        rs.getString("helpCount"),
                        rs.getString("remainingUpdateDate")),
                getUserReviewResParams);
    }

    public List<ReviewImageInfo> reviewImageList(int reviewId){
        String reviewImageListQuery = "select reviewImageUrl from ReviewImage where reviewId = ?";
        int reviewImageListParams = reviewId;
        return this.jdbcTemplate.query(reviewImageListQuery,
                (rs, rowNum) -> new ReviewImageInfo(
                        rs.getString("reviewImageUrl")),
                reviewImageListParams);
    }

    public List<ReviewMenuInfo> reviewMenuInfoList(int reviewId){
        String reviewMenuInfoListQuery = "select m.id as reviewMenuId, m.name as reviewMenuName from OrderPriceInfo i, Menu m\n" +
                "where m.id = i.menuId and\n" +
                "      orderInfoId = (select orderInfoId from Review where id = ?)";
        int reviewMenuInfoListParams = reviewId;
        return this.jdbcTemplate.query(reviewMenuInfoListQuery,
                (rs, rowNum) -> new ReviewMenuInfo(
                        rs.getInt("reviewMenuId"),
                        rs.getString("reviewMenuName")),
                reviewMenuInfoListParams);
    }

    public int checkStoreId(int storeId){
        String checkStoreIdQuery = "select(exists(select * from Store where id = ? and status = 'N'))";
        int checkStoreIdParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreIdQuery, int.class, checkStoreIdParams);
    }

    public GetReviewRes getReviewRes(int storeId){
        String getReviewResQuery = "select s.id as storeId, concat(s.storeName, ' 리뷰') as storeName,\n" +
                "round(avg(score), 1) as totalReviewScore, concat('리뷰 ', count(*), '개') as totalReviewCount\n" +
                "from Review r, Store s\n" +
                "where r.storeId = s.id and s.id = ?";
        int getReviewResParams = storeId;
        return this.jdbcTemplate.queryForObject(getReviewResQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getDouble("totalReviewScore"),
                        rs.getString("totalReviewCount")
                ),
                getReviewResParams
        );
    }

    public List<Review> getReviews(int storeId, String sortQuery){
        String getReviewsQuery = "select r.id as reviewId, replace(u.name, right(u.name,2),'**') as userName, r.score,\n" +
                "case when date_format(CURDATE(), '%Y') > date_format(r.updatedAt, '%Y') then '이전 해' else\n" +
                "case when date_format(CURDATE(), '%m') > date_format(r.updatedAt, '%m') then '지난 달' else\n" +
                "case when date_format(CURDATE(), '%d') = date_format(r.updatedAt, '%d') then '오늘' else\n" +
                "case when date_format(CURDATE(), '%d') - 1 = date_format(r.updatedAt, '%d') then '어제' else\n" +
                "case when date_format(CURDATE(), '%d') - 2 = date_format(r.updatedAt, '%d') then '그제' else\n" +
                "case when date_format(ADDDATE(CURDATE(), - WEEKDAY(CURDATE()) + 0 ), '%d') < date_format(r.updatedAt, '%d') and\n" +
                "date_format(r.updatedAt, '%d') < date_format(ADDDATE( CURDATE(), - WEEKDAY(CURDATE()) + 6 ), '%d') then '이번 주' else '이번 달'\n" +
                "end end end end end end as updatedAt, r.description\n" +
                "from Review r, User u\n" +
                "where r.userId = u.id and r.storeId = ? and r.status = 'N' and u.status = 'N'" + sortQuery;
        int getReviewsParams = storeId;
        return this.jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new Review(
                        rs.getInt("reviewId"),
                        rs.getString("userName"),
                        rs.getInt("score"),
                        rs.getString("updatedAt"),
                        rs.getString("description")),
                getReviewsParams);
    }

    public List<Review> getReviewByPhoto(int storeId, String sortQuery){
        String getReviewByPhotoQuery = "select distinct r.id as reviewId, replace(u.name, right(u.name,2),'**') as userName, r.score,\n" +
                "case when date_format(CURDATE(), '%Y') > date_format(r.updatedAt, '%Y') then '이전 해' else\n" +
                "case when date_format(CURDATE(), '%m') > date_format(r.updatedAt, '%m') then '지난 달' else\n" +
                "case when date_format(CURDATE(), '%d') = date_format(r.updatedAt, '%d') then '오늘' else\n" +
                "case when date_format(CURDATE(), '%d') - 1 = date_format(r.updatedAt, '%d') then '어제' else\n" +
                "case when date_format(CURDATE(), '%d') - 2 = date_format(r.updatedAt, '%d') then '그제' else\n" +
                "case when date_format(ADDDATE(CURDATE(), - WEEKDAY(CURDATE()) + 0 ), '%d') < date_format(r.updatedAt, '%d') and\n" +
                "date_format(r.updatedAt, '%d') < date_format(ADDDATE( CURDATE(), - WEEKDAY(CURDATE()) + 6 ), '%d') then '이번 주' else '이번 달'\n" +
                "end end end end end end as updatedAt, r.description\n" +
                "from Review r, User u, ReviewImage i\n" +
                "where r.id = i.reviewId and r.userId = u.id and r.storeId = ? " +
                "and r.status = 'N' and i.status = 'N' and u.status = 'N'" + sortQuery;
        int getReviewByPhotoParams = storeId;
        return this.jdbcTemplate.query(getReviewByPhotoQuery,
                (rs, rowNum) -> new Review(
                        rs.getInt("reviewId"),
                        rs.getString("userName"),
                        rs.getInt("score"),
                        rs.getString("updatedAt"),
                        rs.getString("description")),
                getReviewByPhotoParams);
    }
}