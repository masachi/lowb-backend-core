package io.github.masachi.utils.preheat;


import io.github.masachi.utils.preheat.mapper.DMPreheatMapper;
import io.github.masachi.utils.preheat.mapper.MySQLPreheatMapper;
import io.github.masachi.utils.preheat.mapper.PGSQLPreheatMapper;
import io.github.masachi.utils.preheat.mapper.PreheatMapper;

/**
 * 数据源驱动
 */
public enum DBDriverMapperType {


    MYSQL("com.mysql.jdbc.jdbc2.optional.MysqlDataSource", MySQLPreheatMapper.class),

    DM("dm.jdbc.driver.DmDriver", DMPreheatMapper.class),

    PGSQL("org.postgresql.ds.PGSimpleDataSource", PGSQLPreheatMapper.class);


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
