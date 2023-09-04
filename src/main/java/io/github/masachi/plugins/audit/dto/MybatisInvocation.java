package io.github.masachi.plugins.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

@Data
@AllArgsConstructor
public class MybatisInvocation {
	private Object[] args;
	private MappedStatement mappedStatement;
	private Object parameter;
	private Executor executor;
}
