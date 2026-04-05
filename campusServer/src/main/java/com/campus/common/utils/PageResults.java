package com.campus.common.utils;

import com.campus.common.result.PageResult;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.function.Function;

public final class PageResults {

    private PageResults() {
    }

    public static <E, D> PageResult<D> from(Page<E> page, long pageNum, long pageSize, Function<E, D> mapper) {
        PageResult<D> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(page.getContent().stream().map(mapper).toList());
        return result;
    }

    public static <D> PageResult<D> empty(long pageNum, long pageSize) {
        PageResult<D> result = new PageResult<>();
        result.setTotal(0);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(Collections.emptyList());
        return result;
    }
}

