package com.example.demo.src.address;

import com.example.demo.src.address.model.Address;
import com.example.demo.src.address.model.GetAddressRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AddressDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUserAddress(Address address) {
        String createAddressQuery = "insert into UserAddress (userId, latitude, longitude, title, roadNameAddress, detailAddress, guideRoad, addressType, nickname) VALUES (?,?,?,?,?,?,?,?,?)";
        Object[] createAddressParams = new Object[]{address.getUserId(), address.getLatitude(), address.getLongitude(), address.getTitle(),
                address.getRoadNameAddress(), address.getDetailAddress(), address.getGuideRoad(), address.getAddressType(), address.getNickname()};
        this.jdbcTemplate.update(createAddressQuery, createAddressParams);
    }

    public int checkUserStatusByUserId(int userId) {
        String checkUserStatusByUserIdQuery = "select exists(select * from User where id = ? and status = 'N')";
        int checkUserStatusByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserStatusByUserIdQuery, int.class, checkUserStatusByUserIdParams);
    }

    public void modifyAddressActiveN(int userId) {
        String modifyAddressActiveNQuery = "update UserAddress set activeYn = 'N' where userId = ? and status = 'N' and activeYn = 'Y'";
        int modifyAddressActiveNParams = userId;
        this.jdbcTemplate.update(modifyAddressActiveNQuery, modifyAddressActiveNParams);
    }

    public void modifyAddressTypeHouse(int userId){
        String modifyAddressTypeHouseQuery = "update UserAddress set addressType ='E' where userId = ? and status = 'N' and addressType = 'H'";
        int modifyAddressTypeHouseParams = userId;
        this.jdbcTemplate.update(modifyAddressTypeHouseQuery, modifyAddressTypeHouseParams);
    }

    public void modifyAddressTypeCompany(int userId){
        String modifyAddressTypeCompanyQuery = "update UserAddress set addressType ='E' where userId = ? and status = 'N' and addressType = 'C'";
        int modifyAddressTypeCompanyParams = userId;
        this.jdbcTemplate.update(modifyAddressTypeCompanyQuery, modifyAddressTypeCompanyParams);
    }

    public List<GetAddressRes> getAddresses(int userId) {
        String getAddressesQuery = "select id as addressId, case when addressType = 'H' then '집' else " +
                "case when addressType = 'C' then '회사' else title end " +
                "end as title, concat(roadNameAddress, ' ', detailAddress) as detailAddress\n" +
                "from UserAddress where userId = ? and status ='N' order by field(addressType, 'C', 'H') desc, id desc";
        int getAddressesParams = userId;
        return this.jdbcTemplate.query(getAddressesQuery,
                (rs, rowNum) -> new GetAddressRes(
                        rs.getInt("addressId"),
                        rs.getString("title"),
                        rs.getString("detailAddress")),
                getAddressesParams);
    }

    public void modifyAddressActiveY(int userId, int addressId) {
        String modifyAddressActiveYQuery = "update UserAddress set activeYn = 'Y' where status = 'N' and userId = ? and id = ?";
        Object[] modifyAddressActiveYParams = new Object[]{userId, addressId};
        this.jdbcTemplate.update(modifyAddressActiveYQuery, modifyAddressActiveYParams);
    }

    public int checkAddressId(int userId, int addressId) {
        String checkAddressIdQuery = "select(exists(select * from UserAddress where status = 'N' and userId = ? and id = ?))";
        Object[] checkAddressIdParams = new Object[]{userId, addressId};
        return this.jdbcTemplate.queryForObject(checkAddressIdQuery, int.class, checkAddressIdParams);
    }
}