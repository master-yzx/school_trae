package com.campus.modules.campus.mapper;

import com.campus.modules.campus.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampusRepository extends JpaRepository<Campus, Long> {

    List<Campus> findByEnabledTrueOrderByIdAsc();

    Optional<Campus> findFirstByName(String name);
}

