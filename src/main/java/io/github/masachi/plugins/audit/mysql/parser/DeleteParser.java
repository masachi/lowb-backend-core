package io.github.masachi.plugins.audit.mysql.parser;

import io.github.masachi.plugins.audit.dto.ChangeColumnData;
import io.github.masachi.plugins.audit.dto.ChangeRowData;
import io.github.masachi.plugins.audit.dto.MybatisInvocation;
import io.github.masachi.plugins.audit.dto.SqlParserInfo;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DeleteParser extends AbstractParser {

	@Override
	public List<ChangeRowData> parseBefore(SqlCommandType sqlCommandType, MybatisInvocation mybatisInvocation) throws Throwable {
		MappedStatement mappedStatement = mybatisInvocation.getMappedStatement();
		BoundSql boundSql = mappedStatement.getBoundSql(mybatisInvocation.getParameter());
		String sql = boundSql.getSql();
		SqlParserInfo sqlParserInfo = new SqlParserInfo(sql, SqlCommandType.DELETE);
		DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
		// 获取要删除的数据
		List<Map<String, Object>> beforeResults = query(mybatisInvocation, boundSql, sqlParserInfo);
		List<ChangeRowData> results = buildChangeDatas(beforeResults, sqlParserInfo);
		return results;
	}

	private List<Map<String, Object>> query(MybatisInvocation mybatisInvocation,
										   BoundSql boundSql, SqlParserInfo sqlParserInfo) throws SQLException {
		return query(mybatisInvocation, boundSql, sqlParserInfo, -1);
	}

	private List<ChangeRowData> buildChangeDatas(List<Map<String, Object>> beforeResults, SqlParserInfo sqlParserInfo) {
		List<ChangeRowData> changeRowDatas = new ArrayList<>();
		if(BaseUtil.isEmpty(beforeResults)){
			return changeRowDatas;
		}
		for (Map<String, Object> beforeDataMap : beforeResults) {
			ChangeRowData changeRowData = buildChangeDataForDelete(beforeDataMap);
			changeRowData.setTableName(sqlParserInfo.getTableName());
			changeRowData.setChangeColumnMap(changeRowData.beforeToChangeColumnMap());
			changeRowDatas.add(changeRowData);
		}
		return changeRowDatas;
	}

	private ChangeRowData buildChangeDataForDelete(final Map<String, Object> beforeDataMap) {
		List<ChangeColumnData> columnList = dataMapToColumnDataList(beforeDataMap);
		ChangeRowData changeData = new ChangeRowData();
		changeData.setBeforeColumnList(columnList);
		return changeData;
	}
}
