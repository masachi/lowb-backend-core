package io.github.masachi.plugins.audit;

import lombok.Data;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.HashMap;


public interface DataSourceBaseParser {
    DataParser getDataParser(SqlCommandType sqlCommandType);
}
