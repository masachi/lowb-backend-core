package io.github.masachi.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.github.masachi.utils.cache.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdempotentUtils {

    private static CacheTemplate<String> cacheTemplate;

    @Autowired
    public void setCacheTemplate(CacheTemplate<String> cacheTemplate) {
        IdempotentUtils.cacheTemplate = cacheTemplate;
    }

    private static int EXPIRE_TIME = 3600 * 24 * 1000;

    public static String generateIdempotentKey(String suffix) {
        StringBuilder idempotentKey = new StringBuilder();
        idempotentKey.append(NanoIdUtils.randomNanoId());

        if(BaseUtil.isNotEmpty(suffix)) {
            idempotentKey.append("-");
            idempotentKey.append(suffix);
        }

        saveIdempotentKey(idempotentKey.toString());

        return idempotentKey.toString();
    }

    public static String generateIdempotentKey() {
        return generateIdempotentKey(null);
    }

    public static Boolean validateIdempotentKey(String idempotentKey) {
        Object idempotentKeyValue = null;

        if(BaseUtil.isEmpty(idempotentKey)) {
            return true;
        }

        idempotentKeyValue = cacheTemplate.fetch(idempotentKey);
        cacheTemplate.delete(idempotentKey);

        if(BaseUtil.isEmpty(idempotentKeyValue)) {
            return false;
        } else if(idempotentKeyValue.toString().equalsIgnoreCase("0")) {
            return false;
        } else if (idempotentKeyValue.toString().equalsIgnoreCase("1")) {
            return true;
        } else {
            return true;
        }
    }

    public static void saveIdempotentKey(String idempotentKey) {
        cacheTemplate.save(idempotentKey, "1", EXPIRE_TIME);
    }
}
