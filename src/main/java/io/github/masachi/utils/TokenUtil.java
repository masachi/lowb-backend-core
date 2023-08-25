package io.github.masachi.utils;

import io.github.masachi.config.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
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

    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(secretString);
    }
}
