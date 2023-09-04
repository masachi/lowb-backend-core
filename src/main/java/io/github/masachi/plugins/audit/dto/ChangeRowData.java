package io.github.masachi.plugins.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRowData {

	/** 数据库表名 */
	public String tableName;

	/** 更改的所有属性的以及它对应的原始值,即更改之前的值*/
	public List<ChangeColumnData> beforeColumnList;

	/**更改的所有属性和值*/
	public List<ChangeColumnData> afterColumnList;

	/** 发生变化的对象的属性及值的键值对*/
	public Map<String, ChangeData> changeColumnMap;

	public Map<String, ChangeData> beforeToChangeColumnMap(){
		List<ChangeColumnData> beforeList = this.beforeColumnList;
		if(beforeList == null || beforeList.size() <= 0){
			return null;
		}
		Map<String, ChangeData> map = new HashMap<>();
		for(ChangeColumnData before : beforeList){
			String name = before.getName();
			Object value = before.getValue();
			map.put(name, new ChangeData(name, value, null));
		}
		return map;
	}

	public Map<String, ChangeData> afterToChangeColumnMap(){
		List<ChangeColumnData> afterList = this.afterColumnList;
		if(afterList == null || afterList.size() <= 0){
			return null;
		}
		Map<String, ChangeData> map = new HashMap<>();
		for(ChangeColumnData after : afterList){
			String name = after.getName();
			Object value = after.getValue();
			map.put(name, new ChangeData(name, null, value));
		}
		return map;
	}
}
