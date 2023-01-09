package io.github.masachi.utils.preheat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DMPreheatMapper extends PreheatMapper {

    @Override
    @Select("SELECT count(1) FROM SYS.POLICY_GROUPS")
    Long querySchema();
}
