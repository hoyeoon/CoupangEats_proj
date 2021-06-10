package com.example.demo.src.cart;

import com.example.demo.src.cart.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CartDao {
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

    public GetMenuInfoRes getMenuInfoRes(int menuId) {
        String getMenuInfoResQuery = "select id as menuId, menuImageUrl, name as menuName, description as menuDescription\n" +
                "from Menu where status = 'N' and id = ?";
        int getMenuInfoResParams = menuId;
        return this.jdbcTemplate.queryForObject(getMenuInfoResQuery,
                (rs, rowNum) -> new GetMenuInfoRes(
                        rs.getInt("menuId"),
                        rs.getString("menuImageUrl"),
                        rs.getString("menuName"),
                        rs.getString("menuDescription")),
                getMenuInfoResParams
        );
    }

    public GetMenuPriceRes getMenuPriceRes(int menuId, int count) {
        String getMenuPriceResQuery = "select id as menuId, concat(format(price * ?, 0), '원') as menuPrice " +
                "from Menu where status = 'N' and id = ?";
        Object[] getMenuPriceResParams = new Object[]{count, menuId};
        return this.jdbcTemplate.queryForObject(getMenuPriceResQuery,
                (rs, rowNum) -> new GetMenuPriceRes(
                        rs.getInt("menuId"),
                        rs.getString("menuPrice")),
                getMenuPriceResParams
        );
    }

    public int checkMenuId(int menuId) {
        String checkMenuIdQuery = "select(exists(select * from Menu where status = 'N' and id = ?))";
        int checkMenuIdParams = menuId;
        return this.jdbcTemplate.queryForObject(checkMenuIdQuery, int.class, checkMenuIdParams);
    }

    public int checkCartId(int userId) {
        String checkCartIdQuery = "select(exists(select * from Cart where userId = ? and status = 'N'))";
        int checkCartIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkCartIdQuery, int.class, checkCartIdParams);
    }

    public int makeCart(int userId) {
        String makeCartQuery = "insert into Cart (userId) values (?)";
        int makeCartParams = userId;
        this.jdbcTemplate.update(makeCartQuery, makeCartParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int checkCartMenuId(int userId, int menuId) {
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where menuId = ? and status = 'N' and userId = ?))";
        Object[] checkCartMenuIdParams = new Object[]{menuId, userId};
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }

    public void makeCartMenu(int userId, int cartId, PostCartReq postCartReq) {
        String makeCartMenuQuery = "insert into CartMenu (userId, cartId, menuId, count) values (?,?,?,?)";
        Object[] makeCartMenuParams = new Object[]{userId, cartId, postCartReq.getMenuId(), postCartReq.getCount()};
        this.jdbcTemplate.update(makeCartMenuQuery, makeCartMenuParams);
    }

    public void modifyCartMenuCount(int userId, PostCartReq postCartReq) {
        String modifyCartMenuCountQuery = "update CartMenu set count = count + ? where menuId = ? and userId = ? and status = 'N'";
        Object[] modifyCartMenuCountParams = new Object[]{postCartReq.getCount(), postCartReq.getMenuId(), userId};
        this.jdbcTemplate.update(modifyCartMenuCountQuery, modifyCartMenuCountParams);
    }

    public int checkSameStoreMenu(int userId, int menuId) {
        String checkSameStoreMenuQuery = "select case when (select storeId from Menu where id = ?) = \n" +
                "(select storeId from Menu where id = (select menuId from CartMenu where userId = ? and status = 'N' limit 1) and status = 'N')\n" +
                "then 1 else 0 end";
        Object[] checkSameStoreMenuParams = new Object[]{menuId, userId};
        return this.jdbcTemplate.queryForObject(checkSameStoreMenuQuery, int.class, checkSameStoreMenuParams);
    }

    public int getCartId(int userId){
        String getCartIdQuery = "select id from Cart where userId = ? order by id DESC limit 1";
        int getCartIdParams = userId;
        return this.jdbcTemplate.queryForObject(getCartIdQuery, int.class, getCartIdParams);
    }

    public int checkCartMenuIdByUserId(int userId){
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where userId = ? and status = 'N'))";
        int checkCartMenuIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }

    public int checkCartMenuIdByCartMenuId(int userId, int cartMenuId){
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where userId = ? and id = ? and status = 'N'))";
        Object[] checkCartMenuIdParams = new Object[]{userId, cartMenuId};
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }

    public void deleteCartMenuByCartMenuId(int userId, int cartMenuId){
        String deleteCartMenuByCartMenuIdQuery = "delete from CartMenu where id = ? and userId = ?";
        Object[] deleteCartMenuByCartMenuIdParams = new Object[]{cartMenuId, userId};
        this.jdbcTemplate.update(deleteCartMenuByCartMenuIdQuery, deleteCartMenuByCartMenuIdParams);
    }

    public void deleteCartMenuByUserId(int userId){
        String deleteCartMenuByUserIdQuery = "delete from CartMenu where userId = ?";
        int deleteCartMenuByUserIdParams = userId;
        this.jdbcTemplate.update(deleteCartMenuByUserIdQuery, deleteCartMenuByUserIdParams);
    }

    public void deleteCartByUserId(int userId){
        String deleteCartByUserIdQuery = "delete from Cart where userId = ?";
        int deleteCartByUserIdParams = userId;
        this.jdbcTemplate.update(deleteCartByUserIdQuery, deleteCartByUserIdParams);
    }

    public GetAddressRes getAddressRes(int userId){
        String getAddressResQuery = "select id as addressId, concat((case when addressType = 'H' then '집' else\n" +
                "    case when addressType = 'C' then '회사' else title end end), ' (으)로 배달') as addressTitle,\n" +
                "       concat(roadNameAddress, ' ', detailAddress) as detailAddress\n" +
                "from UserAddress\n" +
                "where userId = ? and status = 'N' and activeYn = 'Y'";
        int getAddressResParams = userId;
        return this.jdbcTemplate.queryForObject(getAddressResQuery,
                (rs, rowNum) -> new GetAddressRes(
                        rs.getInt("addressId"),
                        rs.getString("addressTitle"),
                        rs.getString("detailAddress")),
                getAddressResParams);
    }

    public GetStoreInfoRes getStoreInfoRes(int userId) {
        String getStoreInfoResQuery = "select distinct s.id as storeId, s.storeName, s.newStoreYn, s.cheetahDeliveryYn\n" +
                "from CartMenu cm, Menu m, Store s\n" +
                "where cm.menuId = m.id and m.storeId = s.id and userId = ? and cm.status = 'N' and m.status = 'N' and s.status = 'N'";
        int getStoreInfoResParams = userId;
        return this.jdbcTemplate.queryForObject(getStoreInfoResQuery,
                (rs, rowNum) -> new GetStoreInfoRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("newStoreYn"),
                        rs.getString("cheetahDeliveryYn")),
                getStoreInfoResParams
        );
    }

    public List<GetCartMenusRes> getCartMenusRes(int userId){
        String cartMenusListQuery = "select cm.id as cartMenuId, m.name as cartMenuName, count as cartMenuCount, concat(format(count * price, 0), '원') as totalPrice\n" +
                "from CartMenu cm, Menu m\n" +
                "where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N'";
        int cartMenusListParams = userId;
        return this.jdbcTemplate.query(cartMenusListQuery,
                (rs, rowNum) -> new GetCartMenusRes(
                        rs.getInt("cartMenuId"),
                        rs.getString("cartMenuName"),
                        rs.getInt("cartMenuCount"),
                        rs.getString("totalPrice")),
                cartMenusListParams
        );
    }

    public List<GetDiscountCouponRes> getDiscountCouponRes(int userId){
        String getDiscountCouponResQuery = "select id as couponId, name as couponName, concat(format(discountPrice, 0), '원 할인') as discountPrice,\n" +
                "       concat(format(minOrderPrice, 0), '원 이상 주문 시') as minOrderPrice,\n" +
                "       concat(date_format(expireDate, '%c/%e'), ' 까지') as expireDate\n" +
                "from UserCoupon\n" +
                "where userId = ? and status = 'N'";
        int getDiscountCouponResParams = userId;
        return this.jdbcTemplate.query(getDiscountCouponResQuery,
                (rs, rowNum) -> new GetDiscountCouponRes(
                        rs.getInt("couponId"),
                        rs.getString("couponName"),
                        rs.getString("discountPrice"),
                        rs.getString("minOrderPrice"),
                        rs.getString("expireDate")),
                getDiscountCouponResParams
        );
    }

    public int checkUsableCoupon(int userId, int couponId){
        String checkUsableCouponQuery = "select(exists(select * from UserCoupon u, StoreCoupon s\n" +
                "where u.userId = ? and u.id = ?\n" +
                "  and s.storeId = (select distinct m.storeId from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N')\n" +
                "  and u.minOrderPrice < (select sum(cm.count * m.price) from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N')\n" +
                "  and u.storeId = s.storeId and u.status = 'N' and s.status = 'N'))";
        Object[] checkUsableCouponParams = new Object[] {userId, couponId, userId, userId};
        return this.jdbcTemplate.queryForObject(checkUsableCouponQuery, int.class, checkUsableCouponParams);
    }

    public String getOrderPrice(int userId){
        String getOrderPriceQuery = "select concat(format(sum(cm.count * m.price), 0), '원') as orderPrice\n" +
                "from CartMenu cm, Menu m\n" +
                "where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N'";
        int getOrderPriceParams = userId;
        return this.jdbcTemplate.queryForObject(getOrderPriceQuery, String.class, getOrderPriceParams);
    }

    public int getOrderPriceIntValue(int userId){
        String getOrderPriceIntValueQuery = "select sum(cm.count * m.price) " +
                "from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N'";
        int getOrderPriceIntValueParams = userId;
        return this.jdbcTemplate.queryForObject(getOrderPriceIntValueQuery, int.class, getOrderPriceIntValueParams);
    }

    public String getDeliveryFee(int userId){
        String getDeliveryFeeQuery = "select concat('+', format(min(case when (select sum(cm.count * m.price) from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N') > minOrderPrice\n" +
                "    then deliveryFee end), 0), '원') as deliveryFee\n" +
                "from DeliveryFeePolicy p\n" +
                "where p.storeId = (select storeId from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N' limit 1)\n" +
                "order by deliveryFee DESC;";
        Object[] getDeliveryFeeParams = new Object[] {userId, userId};
        return this.jdbcTemplate.queryForObject(getDeliveryFeeQuery, String.class, getDeliveryFeeParams);
    }

    public int getDeliveryFeeIntValue(int userId){
        String getDeliveryFeeIntValueQuery = "select min(case when (select sum(cm.count * m.price) from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N') > minOrderPrice\n" +
                "    then deliveryFee end) as deliveryFee\n" +
                "from DeliveryFeePolicy p\n" +
                "where p.storeId = (select storeId from CartMenu cm, Menu m where cm.menuId = m.id and userId = ? and cm.status = 'N' and m.status ='N' limit 1)\n" +
                "order by deliveryFee DESC";
        Object[] getDeliveryFeeIntValueParams = new Object[] {userId, userId};
        return this.jdbcTemplate.queryForObject(getDeliveryFeeIntValueQuery, int.class, getDeliveryFeeIntValueParams);
    }

    public int checkBalanceWithCoupon(int userId, int accountId, int totalPrice, int couponId){
        String checkBalanceWithCouponQuery = "select case when balance >= ? - (select discountPrice from UserCoupon where id = ? and status = 'N') \n" +
                "    then 1 else 0 end\n" +
                "from Account a\n" +
                "where a.userId = ? and a.id = ? and a.status = 'N'";
        Object[] checkBalanceWithCouponParams = new Object[]{totalPrice, couponId, userId, accountId};
        return this.jdbcTemplate.queryForObject(checkBalanceWithCouponQuery, int.class, checkBalanceWithCouponParams);
    }

    public int checkBalance(int userId, int accountId, int totalPrice){
        String checkBalanceQuery = "select case when balance >= ? then 1 else 0 end\n" +
                "from Account a\n" +
                "where a.userId = ? and a.id = ? and a.status = 'N'";
        Object[] checkBalanceParams = new Object[]{totalPrice, userId, accountId};
        return this.jdbcTemplate.queryForObject(checkBalanceQuery, int.class, checkBalanceParams);
    }

    public void withdrawAccountWithCoupon(int userId, int accountId, int totalPrice, int couponId){
        String withdrawAccountWithCouponQuery = "update Account \n" +
                "set balance = balance - (? - (select discountPrice from UserCoupon where id = ? AND status = 'N')) \n" +
                "where userId = ? and id = ? and status = 'N'";
        Object[] withdrawAccountWithCouponParams = new Object[]{totalPrice, couponId, userId, accountId};
        this.jdbcTemplate.update(withdrawAccountWithCouponQuery, withdrawAccountWithCouponParams);
    }

    public void deleteCoupon(int couponId){
        String deleteCouponQuery = "update UserCoupon set status = 'Y' where id = ?";
        int deleteCouponParams = couponId;
        this.jdbcTemplate.update(deleteCouponQuery, deleteCouponParams);
    }

    public void withdrawAccount(int userId, int accountId, int totalPrice){
        String withdrawAccountQuery = "update Account set balance = balance - ? where userId = ? and id = ? and status = 'N'";
        Object[] withdrawAccountParams = new Object[]{totalPrice, userId, accountId};
        this.jdbcTemplate.update(withdrawAccountQuery, withdrawAccountParams);
    }

    public String getUserAddress(int userId){
        String getUserAddressQuery = "select concat(roadNameAddress, ' ', detailAddress) as address " +
                "from UserAddress where userId = ? and activeYn = 'Y'";
        int getUserAddressParams = userId;
        return this.jdbcTemplate.queryForObject(getUserAddressQuery, String.class, getUserAddressParams);
    }

    public String getPaymentMethod(int userId, int accountId){
        String getPaymentMethodQuery = "select concat(p.pg , replace(description, right(description, 2), '...'),\n" +
                "    right(replace((replace(cardNo, right(cardNo, 1), '*')),\n" +
                "        substring((replace(cardNo, right(cardNo, 1), '*')), 9, 4), '****'), 8)) as paymentMethod\n" +
                "from Payment p, Account a\n" +
                "where p.accountId = a.id and userId = ? and a.id = ? and a.status = 'N' and p.status = 'N' limit 1";
        Object[] getPaymentMethodParams = new Object[]{userId, accountId};
        return this.jdbcTemplate.queryForObject(getPaymentMethodQuery, String.class, getPaymentMethodParams);
    }

    public int makeOrderInfo(int userId, int storeId, String address, String paymentMethod, PostOrderReq postOrderReq){
        String makeOrderInfoQuery = "insert into OrderInfo(userId,storeId,address,toOwnerMessage,recycledProductYn,toRiderMessage,paymentMethod)\n" +
                "values(?,?,?,?,?,?,?)";
        Object[] makeOrderInfoParams = new Object[]{userId,storeId,address,postOrderReq.getToOwnerMessage(),postOrderReq.getRecycledProductYn(),postOrderReq.getToRiderMessage(),paymentMethod};
        this.jdbcTemplate.update(makeOrderInfoQuery, makeOrderInfoParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public List<CartMenus> cartMenusList(int userId){
        String cartMenusListQuery = "select menuId, count from CartMenu where userId = ?";
        int cartMenusListParams = userId;
        return this.jdbcTemplate.query(cartMenusListQuery,
                (rs, rowNum) -> new CartMenus(
                        rs.getInt("menuId"),
                        rs.getInt("count")),
                cartMenusListParams
        );
    }

    public void makeOrderInfoPrice(int orderInfoId, int menuId, int count){
        String makeOrderInfoPriceQuery = "insert into OrderPriceInfo(orderInfoId, menuId, count) values(?,?,?)";
        Object[] makeOrderInfoPriceParams = new Object[]{orderInfoId, menuId, count};
        this.jdbcTemplate.update(makeOrderInfoPriceQuery, makeOrderInfoPriceParams);
    }

    public int checkAccount(int userId, int accountId){
        String checkAccountQuery = "select(exists(select * from Account where userId = ? and id = ? and status ='N'))";
        Object[] checkAccountParams = new Object[]{userId, accountId};
        return this.jdbcTemplate.queryForObject(checkAccountQuery, int.class, checkAccountParams);
    }

    public int getStoreIdByUserId(int userId){
        String getStoreIdByUserIdQuery = "select storeId from Menu where id = (select menuId from CartMenu where userId = ? limit 1)";
        int getStoreIdByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(getStoreIdByUserIdQuery, int.class, getStoreIdByUserIdParams);
    }
}