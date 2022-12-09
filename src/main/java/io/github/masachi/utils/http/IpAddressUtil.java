package io.github.masachi.utils.http;


import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.SpringBeanUtils;

import javax.servlet.http.HttpServletRequest;

public class IpAddressUtil {
    /**
     * 获取Ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * @param ip -- 待判断的目标IP
     * @param ipRange -- IP区间段,多个区间段逗号分隔 e.g: "180.169.240.64/28,220.196.49.192/29"
     * @return boolean
     */
    public static boolean isInIpRange(String ip, String ipRange) {
        if(BaseUtil.isEmpty(ip) || BaseUtil.isEmpty(ipRange)){
            return false;
        }
        // 本地IP地址默认返回true
        if("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)){
            return true;
        }
        boolean isInRange = false;
        String[] ipSegments = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ipSegments[0]) << 24)
                | (Integer.parseInt(ipSegments[1]) << 16)
                | (Integer.parseInt(ipSegments[2]) << 8)
                | Integer.parseInt(ipSegments[3]);

        String[] ipRangeArray = ipRange.split(",");
        for(String range : ipRangeArray){
            if(!range.contains("/")){
                // 固定IP地址
                isInRange = ip.equals(range);
            }else{
                // 带有掩码的IP区间段
                // 掩码十进制表示方式
                int deciMask = Integer.parseInt(range.replaceAll(".*/", ""));
                // 十进制掩码转换为32位二进制数对应的int值
                int subNetMask = 0xFFFFFFFF << (32 - deciMask);

                String rangeIp = range.replaceAll("/.*", "");
                String[] rangeIpSegments = rangeIp.split("\\.");

                int cidrIpAddr = (Integer.parseInt(rangeIpSegments[0]) << 24)
                        | (Integer.parseInt(rangeIpSegments[1]) << 16)
                        | (Integer.parseInt(rangeIpSegments[2]) << 8)
                        | Integer.parseInt(rangeIpSegments[3]);

                isInRange = (ipAddr & subNetMask) == (cidrIpAddr & subNetMask);
            }
            if(isInRange){
                break;
            }
        }
        return isInRange;
    }
}
