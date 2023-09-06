package io.github.masachi.plugins.audit;

import io.github.masachi.plugins.audit.parser.DeleteParser;
import io.github.masachi.plugins.audit.parser.InsertParser;
import io.github.masachi.plugins.audit.parser.UpdateParser;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.HashMap;
import java.util.Map;

public class DataParserHolder {

    private static Map<SqlCommandType, DataParser> dataParserMap = new HashMap<>();

    static {
        dataParserMap.put(SqlCommandType.INSERT, new InsertParser());
        dataParserMap.put(SqlCommandType.UPDATE, new UpdateParser());
        dataParserMap.put(SqlCommandType.DELETE, new DeleteParser());
    }

    public static DataParser getDataParser(SqlCommandType sqlCommandType) {
        return dataParserMap.get(sqlCommandType);
    }
}
