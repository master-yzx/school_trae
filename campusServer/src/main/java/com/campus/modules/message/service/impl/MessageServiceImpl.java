package com.campus.modules.message.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.message.dto.MessageDTO;
import com.campus.modules.message.entity.Message;
import com.campus.modules.message.mapper.MessageRepository;
import com.campus.modules.message.service.MessageService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserAccountRepository userAccountRepository;

    private static final int CONTENT_MAX_LENGTH = 500;

    public MessageServiceImpl(MessageRepository messageRepository,
                              UserAccountRepository userAccountRepository) {
        this.messageRepository = messageRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<MessageDTO> pageMessages(String type, Boolean read, long pageNum, long pageSize) {
        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);

        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pn, ps);
        }

        Specification<Message> spec = (root, q, cb) -> cb.equal(root.get("userId"), currentUserId);
        if (type != null && !type.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (read != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("read"), read));
        }

        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps, Sort.by("createdAt").descending());
        Page<Message> page = messageRepository.findAll(spec, pageable);

        return PageResults.from(page, pn, ps, this::toDto);
    }

    @Override
    public void markRead(Long id) {
        Message msg = messageRepository.findById(id).orElse(null);
        if (msg == null) return;
        msg.setRead(Boolean.TRUE);
        messageRepository.save(msg);
    }

    @Override
    public void deleteOne(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void batchMarkRead() {
        List<Message> list = messageRepository.findAll((root, q, cb) -> cb.and(
                cb.equal(root.get("userId"), com.campus.common.auth.CurrentUserHolder.get() != null
                        ? com.campus.common.auth.CurrentUserHolder.get().getId()
                        : -1L),
                cb.equal(root.get("read"), Boolean.FALSE)
        ));
        if (list.isEmpty()) return;
        list.forEach(m -> m.setRead(Boolean.TRUE));
        messageRepository.saveAll(list);
    }

    @Override
    public void batchDeleteRead() {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) return;
        messageRepository.deleteByUserIdAndReadTrue(currentUserId);
    }

    @Override
    public void sendToUser(Long userId, String type, String title, String content) {
        if (userId == null || type == null || type.isBlank() || title == null || title.isBlank()) return;
        String safeTitle = title.length() > 100 ? title.substring(0, 100) : title;
        String safeContent = truncateContent(content);
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setType(type);
        msg.setTitle(safeTitle);
        msg.setContent(safeContent);
        msg.setRead(Boolean.FALSE);
        msg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(msg);
    }

    @Override
    public void sendNoticeToAllUsers(String title, String content) {
        if (title == null || title.isBlank()) return;
        String safeTitle = title.length() > 100 ? title.substring(0, 100) : title;
        String safeContent = truncateContent(content);
        List<UserAccount> users = userAccountRepository.findAll();
        if (users.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        List<Message> messages = users.stream()
                .map(u -> {
                    Message m = new Message();
                    m.setUserId(u.getId());
                    m.setType("SYSTEM");
                    m.setTitle(safeTitle);
                    m.setContent(safeContent);
                    m.setRead(Boolean.FALSE);
                    m.setCreatedAt(now);
                    return m;
                })
                .toList();
        messageRepository.saveAll(messages);
    }

    private String truncateContent(String content) {
        if (content == null) return null;
        return content.length() > CONTENT_MAX_LENGTH ? content.substring(0, CONTENT_MAX_LENGTH) : content;
    }

    private MessageDTO toDto(Message m) {
        MessageDTO dto = new MessageDTO();
        dto.setId(m.getId());
        dto.setType(m.getType());
        dto.setTitle(m.getTitle());
        dto.setContent(m.getContent());
        dto.setCreatedAt(DateTimes.format(m.getCreatedAt()));
        dto.setRead(m.getRead());
        return dto;
    }
}

