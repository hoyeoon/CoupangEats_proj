package com.example.demo.src.main;

import com.example.demo.src.main.model.GetAdvertiseImageRes;
import com.example.demo.src.main.model.GetEventImageRes;
import com.example.demo.src.main.model.GetStoreCategoryRes;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MainDao {
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

    public String getMainAddress(int userId){
        String getMainAddressQuery = "select case when addressType = 'C' then '회사'\n" +
                "    else case when addressType = 'H' then '집' else title end end as title\n" +
                "from UserAddress where userId = ? and status = 'N' and activeYn = 'Y'";
        int getMainAddressParams = userId;
        return this.jdbcTemplate.queryForObject(getMainAddressQuery, String.class, getMainAddressParams);
    }

    public List<GetEventImageRes> getEventImageRes() {
        String getPwdQuery = "select id as eventId, eventImageUrl from EventImage where status = 'N'";
        return this.jdbcTemplate.query(getPwdQuery,
                (rs, rowNum) -> new GetEventImageRes(
                        rs.getInt("eventId"),
                        rs.getString("eventImageUrl"))
        );
    }

    public List<GetStoreCategoryRes> getStoreCategoryRes() {
        String getStoreCategoryResQuery = "select id as storeCategoryId, storeCategoryImageUrl, name as storeCategoryName\n" +
                "from StoreCategory where status = 'N';";
        return this.jdbcTemplate.query(getStoreCategoryResQuery,
                (rs, rowNum) -> new GetStoreCategoryRes(
                        rs.getInt("storeCategoryId"),
                        rs.getString("storeCategoryImageUrl"),
                        rs.getString("storeCategoryName"))
        );
    }

    public GetAdvertiseImageRes getAdvertiseImageRes(){
        String getAdvertiseImageResQuery = "select id as advertiseId, advertiseImageUrl from AdvertiseImage where status = 'N' limit 1";
        return this.jdbcTemplate.queryForObject(getAdvertiseImageResQuery,
                (rs, rowNum) -> new GetAdvertiseImageRes(
                        rs.getInt("advertiseId"),
                        rs.getString("advertiseImageUrl"))
        );
    }
}