package com.campus.common.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private long pageNum;
    private long pageSize;
    private List<T> list;
}

