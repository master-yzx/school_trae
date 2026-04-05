package com.campus.modules.user.mapper;

import com.campus.modules.user.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {

    Page<UserAccount> findByStatus(String status, Pageable pageable);

    Page<UserAccount> findByStatusAndUsernameContainingIgnoreCase(String status, String keyword, Pageable pageable);

    List<UserAccount> findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(String usernameKeyword, String nicknameKeyword);

    Optional<UserAccount> findFirstByUsernameOrPhoneOrStudentNo(String username, String phone, String studentNo);

    Optional<UserAccount> findFirstByPhone(String phone);

    boolean existsByPhone(String phone);
}

