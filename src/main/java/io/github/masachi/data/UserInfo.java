package io.github.masachi.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.masachi.utils.BaseUtil;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 在后台使用
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserInfo implements Serializable {
    public String id;
    public String token;
    public String name;
    public String nickName;
    public String mobile;
    public Boolean needEncyRes = true;
    public String avatar;



    public UserInfo(String id) {
        this.id = id;
    }

    public String getName() {
        if (BaseUtil.isEmpty(name)) {
            return nickName;
        }
        return name;
    }
}
