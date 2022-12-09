package io.github.masachi.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
@PropertySource({"classpath:core.properties"})
public class MD5Util {

    private static String passwordSalt;

    @Value("${password.salt}")
    public void setPasswordSalt(String passwordSalt) {
        MD5Util.passwordSalt = passwordSalt;
    }

    public static String encrypt(String input) {
        return Hex.encodeHexString(DigestUtils.md5(input + passwordSalt));
    }
}
