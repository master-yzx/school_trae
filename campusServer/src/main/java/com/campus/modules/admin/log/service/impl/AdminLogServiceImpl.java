package com.campus.modules.admin.log.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.modules.admin.log.dto.AdminLogDTO;
import com.campus.modules.admin.log.entity.OperationLog;
import com.campus.modules.admin.log.mapper.OperationLogRepository;
import com.campus.modules.admin.log.service.AdminLogService;
import com.campus.common.utils.UploadPaths;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AdminLogServiceImpl implements AdminLogService {

    private final OperationLogRepository operationLogRepository;

    public AdminLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public PageResult<AdminLogDTO> pageList(String operator, String type, String result,
                                            String keyword, String startTime, String endTime,
                                            long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Specification<OperationLog> spec = (root, q, cb) -> cb.conjunction();
        if (operator != null && !operator.isBlank()) {
            String kw = "%" + operator.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("operator"), kw));
        }
        if (type != null && !type.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (result != null && !result.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("result"), result));
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("content"), kw));
        }
        LocalDateTime start = parseStart(startTime);
        if (start != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
        }
        LocalDateTime end = parseEnd(endTime);
        if (end != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), end));
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<OperationLog> page = operationLogRepository.findAll(spec, pageable);

        PageResult<AdminLogDTO> resultPage = new PageResult<>();
        resultPage.setPageNum(pn);
        resultPage.setPageSize(ps);
        resultPage.setTotal(page.getTotalElements());
        resultPage.setList(page.map(this::toDto).getContent());
        return resultPage;
    }

    @Override
    public AdminLogDTO detail(Long id) {
        return operationLogRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public String export(String operator, String type, String result,
                         String keyword, String startTime, String endTime) {
        // 复用与分页相同的筛选逻辑，最多导出近期 5000 条
        Specification<OperationLog> spec = (root, q, cb) -> cb.conjunction();
        if (operator != null && !operator.isBlank()) {
            String kw = "%" + operator.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("operator"), kw));
        }
        if (type != null && !type.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (result != null && !result.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("result"), result));
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.like(root.get("content"), kw));
        }
        LocalDateTime start = parseStart(startTime);
        if (start != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
        }
        LocalDateTime end = parseEnd(endTime);
        if (end != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), end));
        }

        List<OperationLog> logs = operationLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (logs.size() > 5000) {
            logs = logs.subList(0, 5000);
        }

        String filename = "operation-log-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".csv";
        Path uploadDir = UploadPaths.getUploadDir();
        try {
            Files.createDirectories(uploadDir);
            Path file = uploadDir.resolve(filename);

            StringBuilder sb = new StringBuilder();
            // 表头
            sb.append("时间,操作人,类型,结果,IP,内容").append("\n");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (OperationLog log : logs) {
                String time = log.getCreatedAt() != null ? tf.format(log.getCreatedAt()) : "";
                sb.append(escapeCsv(time)).append(',')
                  .append(escapeCsv(log.getOperator())).append(',')
                  .append(escapeCsv(log.getType())).append(',')
                  .append(escapeCsv(log.getResult())).append(',')
                  .append(escapeCsv(log.getIp())).append(',')
                  .append(escapeCsv(log.getContent()))
                  .append("\n");
            }
            // Excel 对纯 UTF-8 CSV 识别不稳定：写入 UTF-8 BOM，避免中文乱码
            byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
            byte[] out = new byte[bom.length + body.length];
            System.arraycopy(bom, 0, out, 0, bom.length);
            System.arraycopy(body, 0, out, bom.length, body.length);
            Files.write(file, out);
            return "/upload/" + filename;
        } catch (Exception e) {
            // 失败时返回空，让上层自行处理提示
            return null;
        }
    }

    private String escapeCsv(String v) {
        if (v == null) return "";
        String s = v.replace("\r", " ").replace("\n", " ");
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private AdminLogDTO toDto(OperationLog log) {
        AdminLogDTO dto = new AdminLogDTO();
        dto.setId(log.getId());
        dto.setOperator(log.getOperator());
        dto.setType(log.getType());
        dto.setContent(log.getContent());
        dto.setIp(log.getIp());
        dto.setResult(log.getResult());
        dto.setCreatedAt(DateTimes.format(log.getCreatedAt()));
        return dto;
    }

    private LocalDateTime parseStart(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseEnd(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim()).atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }
}

