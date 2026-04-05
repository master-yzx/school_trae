package com.campus.modules.campus.service;

import com.campus.modules.campus.dto.CampusDTO;
import com.campus.modules.product.dto.RecommendProductDTO;

import java.util.List;

public interface CampusService {

    List<CampusDTO> listAll();

    List<RecommendProductDTO> listHotProductsByCampus(Long campusId);
}

