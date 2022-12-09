package io.github.masachi.page;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import io.github.masachi.utils.BaseUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 增加pagerhelper的page一个扩展实现，主要是满足之前的前端需要page实体中包含rows等属性
 * 同时提供pagehelper分页的ISelect 接口方式
 * 如果需要其他分页方式，可以接着实现
 **/
@Data
@Builder
@NoArgsConstructor
public class PageInfo<T>{

    @Schema(name = "数据集", description = "数组形式返回")
    private List<T> rows;
    @Schema(name = "总数", description = "long")
    private long total;
    @Schema(name = "当前页", description = "int类型")
    private int page;
    @Schema(name = "每页大小", description = "int类型")
    private int pageSize;


    public <T> PageInfo<T> doSelectPage(ISelect select) {
        select.doSelect();
        //设置记录总数total
        if(BaseUtil.isNotNull(this.getRows())){
            this.setTotal(((Page<T>)(this.getRows())).getTotal());
        }
        return (PageInfo<T>) this;
    }

    public <E> PageInfo<E> doProcessData(IProcessData<T, E> process) {
        List result = process.processData(this.getRows());
        if(BaseUtil.isNotEmpty(result)) {
            this.setRows(result);
        }
        return (PageInfo<E>) this;
    }


    /**
     * 兼容之前老代码的实现
     * @param list
     * @param total
     */
    @Deprecated
    public PageInfo(List<T> list, Long total) {
        this(list, total, 1);
    }

    /**
     * 兼容之前老代码的实现
     * @param list
     * @param total
     * @param pageNum
     */
    @Deprecated
    public PageInfo(List<T> list, Long total, int pageNum) {
        this(list, total, pageNum, 20);
    }

    /**
     * 兼容之前老代码的实现
     * @param list
     * @param total
     * @param pageNum
     * @param pageSize
     */
    @Deprecated
    public PageInfo(List<T> list, Long total, int pageNum, int pageSize) {
        this.rows = list;
        this.total = total;
        this.page = pageNum;
        this.pageSize = pageSize;
    }

}

