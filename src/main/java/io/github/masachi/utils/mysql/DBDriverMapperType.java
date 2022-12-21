package io.github.masachi.utils.mysql;


import io.github.masachi.utils.mysql.mapper.DMPreheatMapper;
import io.github.masachi.utils.mysql.mapper.MySQLPreheatMapper;
import io.github.masachi.utils.mysql.mapper.PreheatMapper;

/**
 * 数据源驱动
 */
public enum DBDriverMapperType {


    MYSQL("com.mysql.jdbc.Driver", MySQLPreheatMapper.class),

    DM("dm.jdbc.driver.DmDriver", DMPreheatMapper.class);


    private String driver;

    private Class<PreheatMapper> mapperClass;


    DBDriverMapperType(String driver, Class mapperClass) {
        this.driver = driver;
        this.mapperClass = mapperClass;
    }

    public String getDriver() {
        return driver;
    }

    public Class<PreheatMapper> getMapperClass() {
        return mapperClass;
    }

    public static DBDriverMapperType of(String tmp) {

        for (DBDriverMapperType each : DBDriverMapperType.values()) {
            if (each.getDriver().equalsIgnoreCase(tmp)) {
                return each;
            }
        }
        return null;
    }
}
