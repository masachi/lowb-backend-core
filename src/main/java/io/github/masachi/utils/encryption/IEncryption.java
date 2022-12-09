package io.github.masachi.utils.encryption;

public interface IEncryption {

    /**
     * 解密
     *
     * @param str
     * @return
     * @throws Exception
     */
    String decode(String str);


    String encode(String str);

}
