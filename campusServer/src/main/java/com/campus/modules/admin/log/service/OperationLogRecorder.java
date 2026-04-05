package com.campus.modules.admin.log.service;

import com.campus.common.auth.CurrentUser;
import com.campus.common.auth.CurrentUserHolder;
import com.campus.modules.admin.log.entity.OperationLog;
import com.campus.modules.admin.log.mapper.OperationLogRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OperationLogRecorder {

    private final OperationLogRepository operationLogRepository;
    private final UserAccountRepository userAccountRepository;

    public OperationLogRecorder(OperationLogRepository operationLogRepository,
                                UserAccountRepository userAccountRepository) {
        this.operationLogRepository = operationLogRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public void record(String type, String content, String ip, boolean success) {
        OperationLog log = new OperationLog();
        log.setType(type);
        log.setContent(content);
        log.setIp(ip);
        log.setResult(success ? "成功" : "失败");
        log.setCreatedAt(LocalDateTime.now());

        String operator = resolveOperator();
        log.setOperator(operator);

        operationLogRepository.save(log);
    }

    private String resolveOperator() {
        CurrentUser cu = CurrentUserHolder.get();
        if (cu == null || cu.getId() == null) return "匿名";
        Long uid = cu.getId();
        try {
            UserAccount u = userAccountRepository.findById(uid).orElse(null);
            if (u == null) return "用户#" + uid;
            String name = (u.getNickname() != null && !u.getNickname().isBlank())
                    ? u.getNickname()
                    : (u.getUsername() != null && !u.getUsername().isBlank() ? u.getUsername() : ("用户#" + uid));
            if (u.getPhone() != null && !u.getPhone().isBlank()) {
                return name + "(" + u.getPhone() + ")";
            }
            return name;
        } catch (Exception e) {
            return "用户#" + uid;
        }
    }
}

