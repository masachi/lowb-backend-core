package io.github.masachi.plugins.audit.mysql.parser;

import io.github.masachi.plugins.audit.dto.*;
import io.github.masachi.plugins.audit.utils.MybatisUtils;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UpdateParser extends AbstractParser {

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Override
    public List<ChangeRowData> parseBefore(SqlCommandType sqlCommandType, MybatisInvocation mybatisInvocation) throws Throwable {
        MappedStatement mappedStatement = mybatisInvocation.getMappedStatement();
        boolean mapUnderscoreToCamelCase = mappedStatement.getConfiguration().isMapUnderscoreToCamelCase();
        Object updateParameterObject = mybatisInvocation.getParameter();
        BoundSql boundSql = mappedStatement.getBoundSql(mybatisInvocation.getParameter());
        DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
        String sql = boundSql.getSql();
        List<SqlParserInfo> sqlParserInfoList = getParsedSqlList(sql, dataSource);
        List<ChangeRowData> results = new ArrayList<ChangeRowData>();
        //从参数中获取更新字段列表
        List<Map<String, Object>> afterMapParameters = MybatisUtils.getParameter(mappedStatement, boundSql, updateParameterObject);
        //获取数据库列名字和实际传入的字段名子的对应关系
        Map<String, String> nameMapping = columnNameToRealNameMapping(mapUnderscoreToCamelCase, sql, boundSql.getParameterMappings());
        boolean isBatch = sqlParserInfoList.size() > 1;
        for (int i = 0; i < sqlParserInfoList.size(); i++) {
            SqlParserInfo sqlParserInfo = sqlParserInfoList.get(i);
            Expression whereExpression = sqlParserInfo.getWhereExpression();
            if (whereExpression == null) {
                log.error("更新语句没有where条件！！！");
                continue;
            }
            //获取更新之前的数据
            List<Map<String, Object>> beforeResults = query(mybatisInvocation, boundSql, sqlParserInfo, isBatch ? i : -1);
            //获取更新之后的参数中的数据
            Map<String, Object> afterDataMap = afterMapParameters.get(i);
            //组装变更列表
            List<ChangeRowData> changeRows = buildChangeDatas(nameMapping, Collections.singletonList(afterDataMap), beforeResults, sqlParserInfo);
            if (!changeRows.isEmpty()) {
                results.addAll(changeRows);
            }
        }
        return results;
    }

    private Map<String, String> columnNameToRealNameMapping(boolean mapUnderscoreToCamelCase, String originalSql, List<ParameterMapping> paramMappings) {
        if (paramMappings == null || paramMappings.size() <= 0) {
            return null;
        }
        Pattern p = Pattern.compile("(?is)([\\w]+)\\s*=\\s*\\?");
        String sqlArr[] = originalSql.split(";");
        List<String> columnNames = new ArrayList<>(10);
        for (String sql : sqlArr) {
            if (StringUtils.isEmpty(sql)) {
                continue;
            }
            Matcher m = p.matcher(sql);
            while (m.find()) {
                String columnName = m.group(1);
                columnNames.add(columnName);
            }
        }
        Map<String, String> mapping = new HashMap<>(columnNames.size());
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String propertyName = paramMappings.get(i).getProperty();
            int dotIdx = propertyName.indexOf(".");
            String realName = dotIdx > 0 ? propertyName.substring(dotIdx + 1) : propertyName;
            mapping.put(mapUnderscoreToCamelCase ? MybatisUtils.mapUnderscoreToCamelCase(columnName) : columnName, realName);
        }
        return mapping;
    }

    /**
     * 解析要审计的SQL
     *
     * @param sql        sql语句
     * @param dataSource 数据源
     */
    private List<SqlParserInfo> getParsedSqlList(String sql, DataSource dataSource) throws Exception {
        //可能是批量操作，有多条update语句
        String[] sqlArr = sql.split(";");
        List<SqlParserInfo> sqlParserInfoList = new ArrayList<>(sqlArr.length);
        for (String subSql : sqlArr) {
            if (StringUtils.isEmpty(subSql.trim())) {
                continue;
            }
            SqlParserInfo sqlParserInfo = new SqlParserInfo(subSql, SqlCommandType.UPDATE);
            sqlParserInfoList.add(sqlParserInfo);
        }
        return sqlParserInfoList;
    }

    private List<ChangeRowData> buildChangeDatas(final Map<String, String> mapping, final List<Map<String, Object>> afterResults, final List<Map<String, Object>> beforeResults, SqlParserInfo sqlParserInfo) {
        List<ChangeRowData> changeDatas = new ArrayList<>();
        if (BaseUtil.isNotEmpty(beforeResults)) {
            for (int index = 0; index < beforeResults.size(); index++) {
                Map<String, Object> beforeDataMap = beforeResults.get(index);
                Map<String, Object> afterDataMap = afterResults.get(index);
                ChangeRowData changeData = buildChangeDataForUpdate(mapping, afterDataMap, beforeDataMap);
                changeData.setTableName(sqlParserInfo.getTableName());
                changeDatas.add(changeData);
            }
        }
        return changeDatas;
    }

    private ChangeRowData buildChangeDataForUpdate(final Map<String, String> mapping, final Map<String, Object> afterDataMap, Map<String, Object> beforeDataMap) {
        ChangeRowData changeData = new ChangeRowData();
        if (beforeDataMap == null) {
            return changeData;
        }
        List<ChangeColumnData> afterColumnList = new ArrayList<>();
        List<ChangeColumnData> beforeColumnList = new ArrayList<>();
        Map<String, ChangeData> changeColumnMap = new HashMap<>();
        for (Map.Entry<String, Object> beforeEntry : beforeDataMap.entrySet()) {
            String beforeKey = beforeEntry.getKey();
            Object beforeValue = beforeEntry.getValue();
            // 保存before
            ChangeColumnData beforeColumn = new ChangeColumnData();
            beforeColumn.setName(beforeKey);
            beforeColumn.setValue(beforeValue);
            beforeColumnList.add(beforeColumn);
            // 保存after
            ChangeColumnData afterColumn = new ChangeColumnData();
            afterColumn.setName(beforeKey);
            if (afterDataMap == null) {
                afterColumn.setValue(beforeValue);
            } else {
                //首先从afterMap中查找
                String afterDataName = "";
                if (afterDataMap.containsKey(beforeKey)) {
                    afterDataName = beforeKey;
                } else {
                    afterDataName = mapping.get(beforeKey);
                }
                if (StringUtils.isEmpty(afterDataName)) {
                    log.error("数据库列名字{}没找到对应的值", beforeKey);
                    continue;
                }
                if (afterDataMap.containsKey(afterDataName)) {
                    Object afterValue = afterDataMap.get(afterDataName);
                    afterColumn.setValue(afterValue);
                    //保存change
                    if (beforeValue != null) {
                        if (beforeValue instanceof Date) {
                            beforeValue = DateFormatUtils.format((Date) beforeValue, dateFormat);
                        }
                    }
                    if (afterValue != null) {
                        if (afterValue instanceof Date) {
                            afterValue = DateFormatUtils.format((Date) afterValue, dateFormat);
                        } else if (afterValue instanceof Boolean) {
                            //before可能是数字 after值是true/false  因为before是从数据库获取的，after是从参数中获取的
                            afterValue = (Boolean) afterValue ? 1 : 0;
                        }
                    }
                    String beforeValueString = beforeValue == null ? "" : beforeValue.toString();
                    String afterValueString = afterValue == null ? "" : afterValue.toString();
                    if (!beforeValueString.equals(afterValueString)) {
                        changeColumnMap.put(beforeKey, new ChangeData(beforeKey, beforeValue, afterValue));
                    }
                }
            }
        }
        if(BaseUtil.isNotEmpty(afterDataMap)) {
            // 捞出来 afterDataMap 中存在 于 beforeMap中的
            for (Map.Entry<String, Object> entry : afterDataMap.entrySet()) {
                String afterKey = entry.getKey();
                String beforeKey = getBeforeKeyFromMapping(mapping, afterKey);
                String key = beforeKey == null ? afterKey : beforeKey;
                String value = entry.getValue() == null ? "" : entry.getValue().toString();
                if (beforeDataMap.containsKey(key)) {
                    changeColumnMap.put(key, new ChangeData(key, value, value));
                }
            }
        }
        changeData.setChangeColumnMap(changeColumnMap);
        changeData.setAfterColumnList(afterColumnList);
        changeData.setBeforeColumnList(beforeColumnList);
        return changeData;
    }

    private String getBeforeKeyFromMapping(Map<String, String> mapping, String key) {
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String beforeKey = entry.getKey();
            String afterKey = entry.getValue();
            if (afterKey.equalsIgnoreCase(key)) {
                return beforeKey;
            }
        }
        return null;
    }

}
