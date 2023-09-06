package io.github.masachi.plugins.audit.parser;
import io.github.masachi.plugins.audit.dto.*;
import io.github.masachi.plugins.audit.utils.MybatisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class InsertParser extends AbstractParser {

	@Override
	public List<ChangeRowData> parseBefore(SqlCommandType sqlCommandType, MybatisInvocation mybatisInvocation) throws Throwable {
		MappedStatement mappedStatement = mybatisInvocation.getMappedStatement();
		Object parameterObject = mybatisInvocation.getParameter();
		BoundSql boundSql = mappedStatement.getBoundSql(mybatisInvocation.getParameter());
		String sql = boundSql.getSql();
		SqlParserInfo sqlParserInfo = new SqlParserInfo(sql, SqlCommandType.INSERT);
		DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
		// 获取插入的字段列表
		List<Map<String, Object>> insertDataMapList = MybatisUtils.getParameter(mappedStatement, boundSql, parameterObject);
		List<ChangeRowData> changeRowDatas = new ArrayList<>();
		for(Map<String, Object> insertDataMap : insertDataMapList){
			ChangeRowData changeData = buildChangeDataForInsert(insertDataMap);
			changeData.setTableName(sqlParserInfo.getTableName());
			changeData.setChangeColumnMap(changeData.afterToChangeColumnMap());
			changeRowDatas.add(changeData);
		}
		return changeRowDatas;
	}

	private ChangeRowData buildChangeDataForInsert(final Map<String, Object> afterDataMap) {
		List<ChangeColumnData> columnList = dataMapToColumnDataList(afterDataMap);
		ChangeRowData changeData = new ChangeRowData();
		changeData.setAfterColumnList(columnList);
		return changeData;
	}
}
