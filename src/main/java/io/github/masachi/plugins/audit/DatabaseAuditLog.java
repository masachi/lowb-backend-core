package io.github.masachi.plugins.audit;

import lombok.Data;

@Data
public class DatabaseAuditLog {

    public static enum OperationEnum
    {
        insert, update, delete
    }

    private Long id;
    private String tableName;
    private String columnName;
    private String operation;
    private Object oldValue;
    private Object newValue;
}
