package io.github.masachi.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @description: 基础工具类
 **/
public final class BaseUtil {

    /**
     * <p>judge object is null</p>
     *
     * @param obj the object
     * @return boolean
     */
    public static boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }

    private BaseUtil() {

    }

    /**
     * <p>judge object is not null</p>
     *
     * @param obj the object
     * @return boolean
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * <p>judge object is equal</p>
     *
     * @param obj1 the object
     * @param obj2 the object
     * @return boolean
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (isNull(obj1) || isNull(obj2)) {
            return false;
        }
        if (obj1.equals(obj2)) {
            return true;
        }
        return false;
    }

    /**
     * <p>judge object is not equal</p>
     *
     * @param obj1 the object
     * @param obj2 the object
     * @return boolean
     */
    public static boolean notEquals(Object obj1, Object obj2) {
        return !equals(obj1, obj2);
    }

    /**
     * <p>string is empty</p>
     *
     * @param str string
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || "".equals(str.trim()) || str.length() <= 0;
    }

    /**
     * <p>string is not empty</p>
     *
     * @param str string
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <p>list<T> is empty</p>
     *
     * @param list string
     * @param <T>  t
     * @return boolean
     */
    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isEmpty(Set<T> set) {
        return set == null || set.isEmpty();
    }

    /**
     * <p>T[] is empty</p>
     *
     * @param array string
     * @param <T>   t
     * @return boolean
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>object is empty</p>
     *
     * @param object
     * @return
     */
    public static boolean isEmpty(Object object) {
        return object == null;
    }

    /**
     * <p>Map is empty</p>
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * <p>list<T> is not empty</p>
     *
     * @param <T>        t
     * @param collection Collection
     * @return boolean
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * judge whether a set is not empty
     *
     * @param set
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(Set<T> set) {
        return !isEmpty(set);
    }

    /**
     * <p>T[] is not empty</p>
     *
     * @param <T>   t
     * @param array string
     * @return boolean
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * <p>object is not empty</p>
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * judge if a Boolean value is true
     * @param value
     * @return
     */
    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    /**
     * Return file extension from file name
     * e.g. file.png => .png
     *
     * @param fileName input file name
     * @return
     */
    public static String fileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Convert a list to the specified type
     *
     * @param sourceList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaList(List sourceList, Class<T> clazz) {
        List<T> list = new ArrayList(sourceList.size());
        ParserConfig config = ParserConfig.getGlobalInstance();
        Iterator var4 = sourceList.iterator();

        while (var4.hasNext()) {
            Object item = var4.next();
            T classItem = TypeUtils.cast(item, clazz, config);
            list.add(classItem);
        }

        return list;
    }

    public static Boolean isURLExist(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            int resCode = conn.getResponseCode();
            conn.disconnect();

            return resCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }

    /**
     * 截取指定长度的字符串
     *
     * @param str 原字符串
     * @param len 长度
     * @return 如果str为null，则返回null；如果str长度小于len，则返回str；如果str的长度大于len，则返回截取后的字符串
     */
    public static String subStrByStrAndLen(String str, int len) {
        return null != str ? str.substring(0, Math.min(str.length(), len)) : null;
    }


    /**
     * 判定是否为数字  此处主要用于对外的接口传参
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
