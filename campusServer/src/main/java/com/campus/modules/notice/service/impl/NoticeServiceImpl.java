package com.campus.modules.notice.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.notice.dto.NoticeDTO;
import com.campus.modules.notice.dto.NoticeSaveRequest;
import com.campus.modules.notice.entity.Notice;
import com.campus.modules.notice.mapper.NoticeRepository;
import com.campus.modules.notice.service.NoticeService;
import com.campus.modules.message.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MessageService messageService;

    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             MessageService messageService) {
        this.noticeRepository = noticeRepository;
        this.messageService = messageService;
    }

    @Override
    public PageResult<NoticeDTO> pagePublic(String type, String keyword, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);
        Specification<Notice> spec = (root, q, cb) -> cb.conjunction();
        spec = spec.and((root, q, cb) -> cb.isTrue(root.get("enabled")));
        if (type != null && !type.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("content"), like)
            ));
        }
        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps,
                Sort.by(Sort.Order.desc("pinned"), Sort.Order.desc("createdAt")));
        Page<Notice> page = noticeRepository.findAll(spec, pageable);
        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public NoticeDTO getPublicDetail(Long id) {
        if (id == null) return null;
        Notice n = noticeRepository.findById(id).orElse(null);
        if (n == null || Boolean.FALSE.equals(n.getEnabled())) return null;
        return toDto(n);
    }

    @Override
    public PageResult<NoticeDTO> pageAdmin(String type, String keyword, Boolean enabled, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);
        Specification<Notice> spec = (root, q, cb) -> cb.conjunction();
        if (type != null && !type.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (enabled != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("enabled"), enabled));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("content"), like)
            ));
        }
        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps,
                Sort.by(Sort.Order.desc("pinned"), Sort.Order.desc("createdAt")));
        Page<Notice> page = noticeRepository.findAll(spec, pageable);
        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public NoticeDTO save(NoticeSaveRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "参数错误");
        String title = request.getTitle() == null ? "" : request.getTitle().trim();
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (title.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "标题不能为空");
        if (content.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "内容不能为空");
        String type = (request.getType() == null || request.getType().isBlank()) ? "SYSTEM" : request.getType().trim();

        Notice n;
        if (request.getId() != null) {
            n = noticeRepository.findById(request.getId()).orElse(null);
            if (n == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
        } else {
            n = new Notice();
            n.setCreatedAt(LocalDateTime.now());
        }

        n.setTitle(title);
        n.setContent(content);
        n.setType(type);
        n.setPinned(request.getPinned() != null ? request.getPinned() : Boolean.FALSE);
        n.setEnabled(request.getEnabled() != null ? request.getEnabled() : Boolean.TRUE);
        n = noticeRepository.save(n);

        // 新发布的公告且已启用时，同步到消息中心，让所有用户在「站内消息」中看到
        if (request.getId() == null && Boolean.TRUE.equals(n.getEnabled())) {
            try {
                messageService.sendNoticeToAllUsers(n.getTitle(), n.getContent());
            } catch (Exception ignored) {
                // 不影响公告保存结果
            }
        }

        return toDto(n);
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;
        noticeRepository.deleteById(id);
    }

    private NoticeDTO toDto(Notice n) {
        NoticeDTO dto = new NoticeDTO();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setContent(n.getContent());
        dto.setType(n.getType());
        dto.setPinned(n.getPinned());
        dto.setEnabled(n.getEnabled());
        dto.setCreatedAt(DateTimes.format(n.getCreatedAt()));
        return dto;
    }
}

