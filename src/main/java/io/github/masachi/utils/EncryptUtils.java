package io.github.masachi.utils;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.github.masachi.utils.encryption.IEncryption;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Log4j2
public class EncryptUtils {

    private IEncryption encryption;

    public EncryptUtils(IEncryption encryption) {
        this.encryption = encryption;
    }

    public String decode(String str) {

        if(StringUtils.isBlank(str)){
            return str;
        }

        String substring = str.substring(0, 51);
        String s2 = str.substring(51, str.length());
        byte[] bytes = Base64Utils.decodeFromString(s2.replaceAll(encryption.decode(substring), ""));
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("解密字符出错，不能按照utf-8处理");
            return "";
        }
    }

    public String encode(String str) {
        String base64Str = Base64Utils.encodeToString(str.getBytes());
        String randomString = getRandomString(10);
        while (base64Str.indexOf(randomString) >= 0) {
            randomString = getRandomString(10);
        }

        String encode = encryption.encode(randomString);
        int keyIndex = getKeyIndex(base64Str);
        return encode + base64Str.substring(0, keyIndex) + randomString + base64Str.substring(keyIndex, base64Str.length());
    }


    private static int getKeyIndex(String data) {
        int number = new BigDecimal(10 * Math.random()).setScale(0, RoundingMode.DOWN).intValue();
        int index = new BigDecimal((data.length() - 1) * number / 10 + "").setScale(0, RoundingMode.DOWN).intValue();
        return index;
    }


    private static String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static String getRandomString(int length) {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet.toCharArray(), length);
    }

    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    public static void sou(int a) {
        int b = a;
        int c = 3;
        System.out.println(a);
    }

}
