package io.github.masachi.config;

import io.github.masachi.condition.MySQLCondition;
import io.github.masachi.utils.mysql.PreheatHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(MySQLCondition.class)
@ConditionalOnBean(PreheatHelper.class)
@Log4j2
public class MySQLPreheatRunner implements CommandLineRunner {

    @Autowired
    PreheatHelper preheatHelper;

    @Override
    public void run(String... args) throws Exception {
        try {
            preheatHelper.preheat();
        } catch (Throwable ignore) {
            log.error(ignore.getMessage());
        }


    }
}