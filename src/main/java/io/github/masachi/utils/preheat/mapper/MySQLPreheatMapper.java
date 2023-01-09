package io.github.masachi.utils.preheat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MySQLPreheatMapper extends PreheatMapper {

    /**
     * 查询数据空间数据-通用
     *
     * @return
     */
    @Override
    @Select("SELECT count(1) FROM information_schema.tables")
    Long querySchema();
}
