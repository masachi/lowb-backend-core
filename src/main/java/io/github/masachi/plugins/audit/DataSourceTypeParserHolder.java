package io.github.masachi.plugins.audit;

import io.github.masachi.plugins.audit.mysql.MysqlDataSourceParser;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.HashMap;
import java.util.Map;

public class DataSourceTypeParserHolder {

    private static Map<DataSourceType, DataSourceBaseParser> dataSourceTypeMap = new HashMap<>();

    static {
        dataSourceTypeMap.put(DataSourceType.MYSQL, new MysqlDataSourceParser());
    }

    public static DataSourceBaseParser getDataSourceParser(DataSourceType dataSourceType) {
        return dataSourceTypeMap.get(dataSourceType);
    }
}
