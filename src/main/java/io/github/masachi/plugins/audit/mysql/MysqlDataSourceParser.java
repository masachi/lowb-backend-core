package io.github.masachi.plugins.audit.mysql;

import io.github.masachi.plugins.audit.DataParser;
import io.github.masachi.plugins.audit.DataSourceBaseParser;
import io.github.masachi.plugins.audit.mysql.parser.DeleteParser;
import io.github.masachi.plugins.audit.mysql.parser.InsertParser;
import io.github.masachi.plugins.audit.mysql.parser.UpdateParser;
import lombok.Data;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.HashMap;

public class MysqlDataSourceParser implements DataSourceBaseParser {

    private HashMap<SqlCommandType, DataParser> dataParser = new HashMap<>();

    public MysqlDataSourceParser() {
        dataParser.put(SqlCommandType.UPDATE, new UpdateParser());
        dataParser.put(SqlCommandType.INSERT, new InsertParser());
        dataParser.put(SqlCommandType.DELETE, new DeleteParser());
    }


    @Override
    public DataParser getDataParser(SqlCommandType sqlCommandType) {
        return dataParser.get(sqlCommandType);
    }
}
