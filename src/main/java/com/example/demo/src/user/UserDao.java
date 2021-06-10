package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (email, name, password, phone) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getName(),
                postUserReq.getPassword(), postUserReq.getPhone()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select id as userId, email, password, name, phone from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userId"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("phone")),
                getPwdParams
        );
    }

    public int checkUserStatusByEmail(String email) {
        String checkUserStatusByEmailQuery = "select exists(select * from User where email = ? and status = 'N')";
        String checkUserStatusByEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkUserStatusByEmailQuery, int.class, checkUserStatusByEmailParams);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select phone from User where phone = ?)";
        String checkPhoneParams = phone;
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkPhoneParams);
    }
}
