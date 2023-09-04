package io.github.masachi.utils.mongodb;

public interface IPersistentSupport {

    /**
     * 用于从mongoDB取出数据之后的包装
     */
    void onLoaded();

}
