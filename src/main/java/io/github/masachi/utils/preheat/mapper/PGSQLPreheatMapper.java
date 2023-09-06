package io.github.masachi.utils.preheat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PGSQLPreheatMapper extends PreheatMapper {

    @Override
    @Select("select count(1) from information_schema.tables")
    Long querySchema();
}
