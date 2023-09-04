package io.github.masachi.plugins.audit.dto;

import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

@Data
public class SqlParserInfo {
	private SqlCommandType actionType;
	private String tableName;
	private Table table;
	private Expression whereExpression;

	//只有update才需要
	private List<UpdateSet> columns;
	private List<Expression> expressions;
	
	public SqlParserInfo(String sql, SqlCommandType actionType) throws JSQLParserException {
		this.actionType = actionType;
		if(sql == null || sql.isEmpty()){
			return ;
		}
		Statement statement = CCJSqlParserUtil.parse(sql);
		if(actionType == SqlCommandType.UPDATE){
			Update updateStatement = (Update) statement;
			this.table = updateStatement.getTable();
			this.whereExpression = updateStatement.getWhere();

			this.columns = updateStatement.getUpdateSets();
			this.expressions = updateStatement.getExpressions();
		}else if(actionType == SqlCommandType.INSERT){
			Insert insertStatement = (Insert) statement;
			Table insertTable = insertStatement.getTable();
			if(insertTable==null ){
				return ;
			}
			this.table = insertTable;
			this.whereExpression = null;
		}else if(actionType == SqlCommandType.DELETE){
			Delete deleteStatement = (Delete) statement;
			Table deleteTables = deleteStatement.getTable();
			if(deleteTables==null ){
				return ;
			}
			this.table = deleteTables;
			this.whereExpression = deleteStatement.getWhere();
		}
		//防止表名中带有schema
		this.tableName = table.getName();
	}
}

