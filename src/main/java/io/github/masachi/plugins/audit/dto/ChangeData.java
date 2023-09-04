package io.github.masachi.plugins.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeData {

    /**列名*/
    private String name;
    /**旧值*/
    private Object oldValue;
    /**新值*/
    private Object newValue;
}
