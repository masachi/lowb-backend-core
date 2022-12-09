package io.github.masachi.page;
import com.github.pagehelper.Page;

/**
 * 为对应前端需要返回值，封装的分页方法
 * 主要是包装了一下pagehelper的startPage 方法
 **/
public class MPageHelper {

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     */
    public static <T> PageInfo<T> startPage(int pageNum, int pageSize) {

        Page<T> page=com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);

        return PageInfo.<T>builder()
                .rows(page.getResult())
                .page(page.getPageNum())
                .pageSize(page.getPageSize())
                .total(page.getTotal())
                .build();
    }



    public static <T> PageInfo<T> startPage(int pageNum, int pageSize, boolean pageSizeZero) {

        Page<T> page=com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        page.pageSizeZero(pageSizeZero);

        return PageInfo.<T>builder()
                .rows(page.getResult())
                .page(page.getPageNum())
                .pageSize(page.getPageSize())
                .total(page.getTotal())
                .build();
    }


}

