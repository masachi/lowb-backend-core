package io.github.masachi.utils.encryption;

import java.util.ArrayList;
import java.util.List;

public class IdEncryptUtil {
    static List<String> str = new ArrayList();

    static {
        str.add("a");
        str.add("b");
        str.add("c");
        str.add("d");
        str.add("e");
        str.add("f");
        str.add("g");
        str.add("h");
        str.add("i");
        str.add("j");
        str.add("k");
        str.add("l");
        str.add("m");
        str.add("n");
        str.add("o");
        str.add("p");
        str.add("q");
        str.add("r");
        str.add("s");
        str.add("t");
        str.add("u");
        str.add("v");
    }

    public static String encode(String value) {
        String result = null;
        if (value != null && !"".equals(value.trim())) {
            result = Base64Utils.encrypt(value.getBytes());
        }

        result = convertStr(result);

        int baseNumber = result.charAt(0);

        StringBuffer randomStr = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            int i1 = baseNumber % 2 + 2 * i;
            randomStr.append(str.get(i1));
        }

        int splitIndex = baseNumber % 4;
        String mixCode = randomStr.substring(4 - splitIndex, 4);
        char[] mixCodeChars = mixCode.toCharArray();

        StringBuffer mixedBuffer = new StringBuffer(result);
        for (int ii = 0; ii < mixCodeChars.length; ii++) {
            int i1 = 2 * (ii + 1) - 1;
            char c = mixCode.charAt(ii);
            mixedBuffer.insert(i1, String.valueOf(c));
        }

        String leftMixCode = randomStr.substring(0, 4 - splitIndex);
        return mixedBuffer.append(leftMixCode).toString();
    }

    public static String decode(String value) {
        char[] chars = value.toCharArray();
        int length = chars.length;

        int indexNumber = -1;
        int strCountIndex = 0;
        int volidLength = 0;
        for (int i = length - 1; i >= 0; i--) {
            char aChar = chars[i];
            if (Character.isDigit(aChar)) {
                indexNumber = new Integer(String.valueOf(aChar)).intValue();
                strCountIndex = length - i - 1;
                volidLength = i;
                break;
            }
        }

        //再就是去去除插入在前面的字符串
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < volidLength; i++) {
            if (i % 2 == 0) {
                stringBuffer.append(chars[i]);
            } else {
                if ((i + 1) / 2 > (4 - strCountIndex)) {
                    stringBuffer.append(chars[i]);
                }
            }
        }

        if (indexNumber > 0) {
            for (int i = 0; i < indexNumber; i++) {
                stringBuffer.append("=");
            }
        }

        value = stringBuffer.toString();
        String result = "";
        try {
            if (value != null && !"".equals(value.trim())) {
                byte[] bytes = Base64Utils.decrypt(value);
                result = new String(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertStr(String str) {
        int result = 0;
        int length = str.length();
        char c1 = str.charAt(length - 1);
        char c2 = str.charAt(length - 2);

        if (String.valueOf(c1).equalsIgnoreCase("=")) {
            result = 1;
        }

        if (String.valueOf(c2).equalsIgnoreCase("=")) {
            result += 1;
        }

        String substring = str.substring(0, length - result);
        return substring + result;
    }
}
