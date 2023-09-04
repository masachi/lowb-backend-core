package io.github.masachi.plugins.audit;

import io.github.masachi.plugins.audit.dto.ChangeRowData;
import io.github.masachi.plugins.audit.dto.MybatisInvocation;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

public interface DataParser {

    /**
     * 在执行修改之前解析数据
     * @param sqlCommandType
     * @param mybatisInvocation
     * @return
     * @throws Throwable
     */
    List<ChangeRowData> parseBefore(SqlCommandType sqlCommandType, MybatisInvocation mybatisInvocation) throws Throwable;

    /**
     * 在执行修改之后解析数据，如insert之后是可以取到insert的对象的id的
     * @param mybatisInvocation
     * @param changeRows
     * @return
     * @throws Throwable
     */
    List<ChangeRowData> parseAfter(MybatisInvocation mybatisInvocation, List<ChangeRowData> changeRows)throws Throwable;

}
