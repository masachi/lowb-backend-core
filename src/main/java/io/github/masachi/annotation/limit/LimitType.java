package io.github.masachi.annotation.limit;

import lombok.Data;


public enum LimitType {

    /**
     * 基于Ip的限制
     */
    IP("IP", "基于Ip的限制"),
    /**
     * 自定义的限制
     */
    CUSTOM("CUSTOM", "自定义的限制"),
    /**
     * 全局限制
     */
    GLOBAL("GLOBAL", "全局限制");


    private String name;
    private String value;

    LimitType(String value, String name) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
