package com.campus.modules.product.service;

import com.campus.modules.product.dto.ProductDetailDTO;
import com.campus.modules.product.dto.ProductMediaDTO;
import com.campus.modules.product.dto.RecommendProductDTO;

import java.util.List;

public interface ProductDetailService {

    ProductDetailDTO getDetail(Long productId);

    List<ProductMediaDTO> listMedia(Long productId);

    List<RecommendProductDTO> listRelated(Long productId);
}

