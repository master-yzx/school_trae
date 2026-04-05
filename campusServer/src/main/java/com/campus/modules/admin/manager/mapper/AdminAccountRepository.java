package com.campus.modules.admin.manager.mapper;

import com.campus.modules.admin.manager.entity.AdminAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {

    Page<AdminAccount> findByOrderByCreatedAtDesc(Pageable pageable);

    Optional<AdminAccount> findFirstByUsername(String username);
}

