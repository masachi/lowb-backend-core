package io.github.masachi.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class BeanCopyUtils {

    /**
     * bean属性复制
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig){

        org.springframework.beans.BeanUtils.copyProperties(orig,dest);

    }

    /**
     * bean属性复制,忽略空属性复制，保持原有值
     * @param dest
     * @param orig
     */
    public static void copyPropertiesIgnoreNull(Object dest, Object orig){

        org.springframework.beans.BeanUtils.copyProperties(orig,dest,getNullPropertyNames(orig));

    }


    /**
     * 获取原Object所有空属性值
     * @param orig
     * @return
     */
    private static String[] getNullPropertyNames (Object orig) {
        final BeanWrapper src = new BeanWrapperImpl(orig);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (BaseUtil.isEmpty(srcValue)) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    /**
     * bean属性复制,且换回实体
     * @param clazz
     * @param orig
     * @param <T>
     * @return
     */
    public static <T> T copyProperties(Class<T> clazz, Object orig){

        if(BaseUtil.isEmpty(orig)){
            return null;
        }
        try {
            T t = clazz.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(orig,t);
            return t;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Bean转化为Map,并复制到目标对象中,同时去除NULL
     * @param dest
     * @param orig
     */
    public static void copyBeanInToMapWithOutNull(Map<String,Object> dest, Object orig){

        if(dest==null){
            dest=new HashMap();
        }

        dest.putAll(convertBeanInToMapWithOutNull(orig));
    }


    public static Map<String,Object> convertBeanInToMapWithOutNull(Object orig){

        BeanMap beanMap = new BeanMap(orig);

        return  beanMap.entrySet().stream()
                .filter(key2Value-> BaseUtil.isNotEmpty(key2Value.getValue()) && !"class".equals(key2Value.getKey()))
                .collect(Collectors.toMap(
                        (e) -> (String) e.getKey(),
                        (e) -> e.getValue()
                ));
    }

}

