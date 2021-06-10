package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.Address;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.KakaoUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackFor = BaseException.class)
public class AddressService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AddressDao addressDao;
    private final AddressProvider addressProvider;
    private final JwtService jwtService;
    private final KakaoUtil kakaoUtil;

    @Autowired
    public AddressService(AddressDao addressDao, AddressProvider addressProvider, JwtService jwtService, KakaoUtil kakaoUtil) {
        this.addressDao = addressDao;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;
        this.kakaoUtil = kakaoUtil;
    }

    public void createUserAddress(PostAddressReq postAddressReq, int userId) throws BaseException {
        String apiKey = "KakaoAK " + kakaoUtil.getKey();
        String location = postAddressReq.getAddress();
        String roadNameAddress = null;
        String lotNoAddress = null;
        BigDecimal latitude = null;
        BigDecimal longitude = null;
        String flag = null;
        try {
            String address = "https://dapi.kakao.com/v2/local/search/address.json";

            location = URLEncoder.encode(location, "UTF-8");

            String query = "query=" + location;

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(address);
            stringBuffer.append("?");
            stringBuffer.append(query);

            URL url = new URL(stringBuffer.toString());
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", apiKey);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
            br.close();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonArray documents = element.getAsJsonObject().get("documents").getAsJsonArray();
            int total_count = element.getAsJsonObject().get("meta").getAsJsonObject().get("total_count").getAsInt();

            if (total_count == 1) {
                flag = "addressSearch";
                latitude = documents.getAsJsonArray().get(0).getAsJsonObject().get("y").getAsBigDecimal();
                longitude = documents.getAsJsonArray().get(0).getAsJsonObject().get("x").getAsBigDecimal();
                roadNameAddress = documents.getAsJsonArray().get(0).getAsJsonObject().get("road_address").getAsJsonObject().get("address_name").getAsString();
                lotNoAddress = documents.getAsJsonArray().get(0).getAsJsonObject().get("address").getAsJsonObject().get("address_name").getAsString();
            }
        } catch (Exception e) {
            throw new BaseException(INVALID_ADDRESS);
        }

        location = postAddressReq.getAddress();
        try {
            String keyword = " https://dapi.kakao.com/v2/local/search/keyword.json";
            location = URLEncoder.encode(location, "UTF-8");
            String query = "query=" + location;

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(keyword);
            stringBuffer.append("?");
            stringBuffer.append(query);

            URL url = new URL(stringBuffer.toString());
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", apiKey);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
            br.close();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonArray documents = element.getAsJsonObject().get("documents").getAsJsonArray();
            int total_count = element.getAsJsonObject().get("meta").getAsJsonObject().get("total_count").getAsInt();

            if (total_count == 1) {
                if(flag == null) {
                    flag = "buildingNameSearch";
                    latitude = documents.getAsJsonArray().get(0).getAsJsonObject().get("y").getAsBigDecimal();
                    longitude = documents.getAsJsonArray().get(0).getAsJsonObject().get("x").getAsBigDecimal();
                    roadNameAddress = documents.getAsJsonArray().get(0).getAsJsonObject().get("road_address_name").getAsString();
                }
            }
        } catch (Exception e) {
            throw new BaseException(INVALID_ADDRESS);
        }

        if(roadNameAddress == null){
            throw new BaseException(INVALID_ADDRESS);
        }

        try {
            if(postAddressReq.getAddressType().equals("H")){
                addressDao.modifyAddressTypeHouse(userId);
            }
            if(postAddressReq.getAddressType().equals("C")){
                addressDao.modifyAddressTypeCompany(userId);
            }
            addressDao.modifyAddressActiveN(userId);
            if(flag.equals("addressSearch")){
                addressDao.createUserAddress(new Address(userId, latitude, longitude, lotNoAddress, roadNameAddress, postAddressReq.getDetailAddress(),
                        postAddressReq.getRoadGuide(), postAddressReq.getAddressType(), postAddressReq.getNickname()));
            }
            else{
                addressDao.createUserAddress(new Address(userId, latitude, longitude, postAddressReq.getAddress(), roadNameAddress, postAddressReq.getDetailAddress(),
                        postAddressReq.getRoadGuide(), postAddressReq.getAddressType(), postAddressReq.getNickname()));
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAddressActiveYn(int userId, int addressId) throws BaseException {
        try{
            addressDao.modifyAddressActiveN(userId);
            addressDao.modifyAddressActiveY(userId, addressId);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}