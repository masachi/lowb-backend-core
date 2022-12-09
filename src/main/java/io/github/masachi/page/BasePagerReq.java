package io.github.masachi.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BasePagerReq {

    @Schema(name = "页码")
    private Integer page = 1;

    @Schema(name = "每页的大小, 0时为全量数据")
    private Integer pageSize = 10;
}
