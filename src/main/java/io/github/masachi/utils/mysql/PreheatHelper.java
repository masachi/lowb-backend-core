package io.github.masachi.utils.mysql;


import io.github.masachi.condition.MySQLCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@Conditional(MySQLCondition.class)
@ConditionalOnClass(name = "org.mybatis.spring.SqlSessionTemplate")
public class PreheatHelper {

    @Autowired
    protected SqlSessionTemplate sqlSession;


    /**
     * 预热
     */
    public Object preheat() {

        if(BaseUtil.isEmpty(sqlSession)){
            return RespVO.error("error");
        }

        if (!this.sqlSession.getSqlSessionFactory().getConfiguration().hasMapper(PreheatMapper.class)) {
            this.sqlSession.getSqlSessionFactory().getConfiguration().addMapper(PreheatMapper.class);
        }

        final Long result = this.sqlSession.getMapper(PreheatMapper.class).querySchema();
        log.info("----------->DB连接预热<-----------");
        return RespVO.success(result);

    }

    @Mapper
    public interface PreheatMapper {

        /**
         * 查询数据空间数据-通用
         *
         * @return
         */
        @Select("SELECT count(1) FROM information_schema.tables")
        Long querySchema();
    }
}
