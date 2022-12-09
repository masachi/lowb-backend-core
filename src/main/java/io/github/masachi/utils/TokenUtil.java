package io.github.masachi.utils;

import io.github.masachi.config.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class TokenUtil {

    /**
     * 生成token
     *
     * @return token
     */
    public static String getToken() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("uuid", UUID.randomUUID().toString());
        return Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis()+TokenConfig.EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, TokenConfig.TOKEN_SECRET).compact();
    }

    /**
     * 生成用户token
     *
     * @return token
     */
    public static String getTokenWithUserInfo(String userId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("uuid", UUID.randomUUID().toString());
        map.put("expirationTime",System.currentTimeMillis()+TokenConfig.EXPIRE_TIME);
        map.put("userId", userId);
        return Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis()+TokenConfig.EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, TokenConfig.TOKEN_SECRET).compact();
    }

    public static String getTokenWithUserInfo(String userId, String uuid) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("uuid", uuid);
        map.put("expirationTime",System.currentTimeMillis()+TokenConfig.EXPIRE_TIME);
        map.put("userId", userId);
        return Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis()+TokenConfig.EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, TokenConfig.TOKEN_SECRET).compact();
    }

    public static Map<String, Object> getDataFromToken(String token) {
        return Jwts.parser().setSigningKey(TokenConfig.TOKEN_SECRET).parseClaimsJws(token).getBody();
    }
}
