package com.campus.modules.order.mapper;

import com.campus.modules.order.entity.OrderMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderMain, Long>, JpaSpecificationExecutor<OrderMain> {

    List<OrderMain> findByGroupId(String groupId);
}

