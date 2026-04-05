package com.campus.modules.product.service;

import com.campus.common.result.PageResult;
import com.campus.modules.product.dto.ProductListItemDTO;
import com.campus.modules.product.dto.ProductSearchQuery;

public interface ProductSearchService {

    PageResult<ProductListItemDTO> search(ProductSearchQuery query);
}

