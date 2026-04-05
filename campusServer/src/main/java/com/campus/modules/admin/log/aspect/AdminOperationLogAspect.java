package com.campus.modules.admin.log.aspect;

import com.campus.common.auth.CurrentUserHolder;
import com.campus.modules.admin.log.service.OperationLogRecorder;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AdminOperationLogAspect {

    private final OperationLogRecorder operationLogRecorder;

    public AdminOperationLogAspect(OperationLogRecorder operationLogRecorder) {
        this.operationLogRecorder = operationLogRecorder;
    }

    @Around("within(com.campus.modules.admin..controller..*)")
    public Object aroundAdminController(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return pjp.proceed();
        }

        String uri = request.getRequestURI();
        // 避免对“查询操作日志本身”打日志，防止噪音
        if (uri != null && uri.startsWith("/api/admin/logs")) {
            return pjp.proceed();
        }

        // 仅记录后台增删改（非 GET）
        String method = request.getMethod();
        if (method == null || "GET".equalsIgnoreCase(method)) {
            return pjp.proceed();
        }

        // 仅对已登录用户记录（后台接口本身需要登录，这里做兜底）
        if (CurrentUserHolder.get() == null) {
            return pjp.proceed();
        }

        String type = resolveType(uri);
        String content = buildContent(method, uri, request.getQueryString());
        String ip = resolveIp(request);

        try {
            Object ret = pjp.proceed();
            operationLogRecorder.record(type, content, ip, true);
            return ret;
        } catch (Throwable ex) {
            operationLogRecorder.record(type, content + " | error=" + safeMsg(ex), ip, false);
            throw ex;
        }
    }

    private HttpServletRequest currentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildContent(String method, String uri, String query) {
        if (query == null || query.isBlank()) return method + " " + uri;
        return method + " " + uri + "?" + query;
    }

    private String resolveIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        String real = request.getHeader("X-Real-IP");
        if (real != null && !real.isBlank()) return real.trim();
        return request.getRemoteAddr();
    }

    private String resolveType(String uri) {
        if (uri == null) return "后台操作";
        if (uri.startsWith("/api/admin/settings")) return "系统配置";
        if (uri.startsWith("/api/admin/orders")) return "订单操作";
        if (uri.startsWith("/api/admin/review")) return "商品审核";
        if (uri.startsWith("/api/admin/categories")) return "分类管理";
        if (uri.startsWith("/api/admin/content")) return "内容管控";
        if (uri.startsWith("/api/admin/users")) return "用户管理";
        if (uri.startsWith("/api/admin/sellers")) return "卖家管理";
        if (uri.startsWith("/api/admin/managers")) return "管理员管理";
        if (uri.startsWith("/api/admin/stats")) return "数据统计";
        if (uri.startsWith("/api/admin/overview")) return "控制台";
        return "后台操作";
    }

    private String safeMsg(Throwable ex) {
        if (ex == null) return "";
        String m = ex.getMessage();
        if (m == null) return ex.getClass().getSimpleName();
        m = m.replace("\n", " ").replace("\r", " ").trim();
        if (m.length() > 200) m = m.substring(0, 200);
        return m;
    }
}

