package io.github.masachi.utils.preheat;


import io.github.masachi.condition.SQLCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.preheat.mapper.PreheatMapper;
import io.github.masachi.vo.RespVO;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@Conditional(SQLCondition.class)
@ConditionalOnClass(name = "org.mybatis.spring.SqlSessionTemplate")
public class PreheatHelper {

    @Value("${spring.datasource.dataSourceClassName}")
    private String driverClassName;

    @Autowired
    protected SqlSessionTemplate sqlSession;


    /**
     * 预热
     */
    public Object preheat() {

        if(BaseUtil.isEmpty(sqlSession)){
            return RespVO.error("error");
        }

        DBDriverMapperType mapper = DBDriverMapperType.of(driverClassName);

        if(BaseUtil.isEmpty(mapper)) {
            log.info("----------->DB连接预热Mapper未找到，不预热<-----------");
            return RespVO.error("error");
        }

        Class<PreheatMapper> preheatMapperClass = mapper.getMapperClass();

        if(BaseUtil.isEmpty(preheatMapperClass)) {
            log.info("----------->DB连接预热Mapper未找到，不预热<-----------");
            return RespVO.error("error");
        }

        if (!this.sqlSession.getSqlSessionFactory().getConfiguration().hasMapper(preheatMapperClass)) {
            this.sqlSession.getSqlSessionFactory().getConfiguration().addMapper(preheatMapperClass);
        }

        final Long result = this.sqlSession.getMapper(preheatMapperClass).querySchema();
        log.info("----------->DB连接预热<-----------");
        return RespVO.success(result);

    }
}
