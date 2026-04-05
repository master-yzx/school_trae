package com.campus.modules.chat.service.impl;

import com.campus.common.result.PageResult;
import com.campus.common.utils.DateTimes;
import com.campus.common.utils.PageResults;
import com.campus.modules.chat.dto.ChatMessageDTO;
import com.campus.modules.chat.dto.ChatSessionDTO;
import com.campus.modules.chat.dto.ChatSessionDetailDTO;
import com.campus.modules.chat.entity.ChatMessage;
import com.campus.modules.chat.entity.ChatSession;
import com.campus.modules.chat.mapper.ChatMessageRepository;
import com.campus.modules.chat.mapper.ChatSessionRepository;
import com.campus.modules.chat.service.ChatService;
import com.campus.modules.order.entity.OrderMain;
import com.campus.modules.order.mapper.OrderRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;

    public ChatServiceImpl(ChatSessionRepository chatSessionRepository,
                           ChatMessageRepository chatMessageRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           UserAccountRepository userAccountRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public PageResult<ChatSessionDTO> pageMySessions(long pageNum, long pageSize) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return PageResults.empty(pageNum, pageSize);
        }

        long pn = Math.max(1, pageNum);
        long ps = Math.max(1, pageSize);
        Pageable pageable = PageRequest.of((int) (pn - 1), (int) ps);
        Page<ChatSession> page = chatSessionRepository
                .findByBuyerIdOrSellerIdOrderByLastTimeDesc(currentUserId, currentUserId, pageable);

        List<Long> productIds = page.getContent().stream()
                .map(ChatSession::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .stream().toList();

        Map<Long, Product> productMap = productIds.isEmpty()
                ? Map.of()
                : productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        Set<Long> otherUserIds = page.getContent().stream()
                .flatMap(s -> java.util.stream.Stream.of(s.getBuyerId(), s.getSellerId()))
                .filter(id -> !Objects.equals(id, currentUserId))
                .collect(Collectors.toSet());

        Map<Long, String> userNameMap = otherUserIds.isEmpty()
                ? Map.of()
                : userAccountRepository.findAllById(otherUserIds).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));

        return PageResults.from(page, pn, ps, s -> toSessionDto(s, currentUserId, productMap, userNameMap));
    }

    @Override
    public ChatSessionDetailDTO getMySession(Long sessionId) {
        if (sessionId == null) return null;
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) {
            return null;
        }

        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) return null;
        if (!Objects.equals(session.getBuyerId(), currentUserId) && !Objects.equals(session.getSellerId(), currentUserId)) {
            return null;
        }

        Map<Long, Product> productMap = Map.of();
        if (session.getProductId() != null) {
            Product p = productRepository.findById(session.getProductId()).orElse(null);
            if (p != null) {
                productMap = Map.of(p.getId(), p);
            }
        }

        Set<Long> otherUserIds = new HashSet<>();
        otherUserIds.add(Objects.equals(session.getBuyerId(), currentUserId) ? session.getSellerId() : session.getBuyerId());
        Map<Long, String> userNameMap = userAccountRepository.findAllById(otherUserIds).stream()
                .collect(Collectors.toMap(UserAccount::getId,
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));

        ChatSessionDTO sessionDTO = toSessionDto(session, currentUserId, productMap, userNameMap);

        List<ChatMessage> msgs = chatMessageRepository.findTop50BySessionIdOrderByCreatedAtDesc(sessionId);
        Collections.reverse(msgs);
        List<ChatMessageDTO> messageDTOs = msgs.stream().map(this::toMessageDto).toList();

        // 打开会话时清空当前用户的未读数
        if (Objects.equals(session.getBuyerId(), currentUserId)) {
            session.setUnreadBuyer(0);
        } else if (Objects.equals(session.getSellerId(), currentUserId)) {
            session.setUnreadSeller(0);
        }
        chatSessionRepository.save(session);

        ChatSessionDetailDTO detail = new ChatSessionDetailDTO();
        detail.setSession(sessionDTO);
        detail.setMessages(messageDTOs);
        return detail;
    }

    @Override
    public Long startByProduct(Long productId) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        if (productId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getSellerId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在");
        Long buyerId = currentUserId;
        Long sellerId = product.getSellerId();
        if (Objects.equals(buyerId, sellerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能咨询自己");
        }
        return getOrCreateSession(buyerId, sellerId, productId).getId();
    }

    @Override
    public Long startByOrder(Long orderId) {
        Long currentUserId = com.campus.common.auth.CurrentUserHolder.get() != null
                ? com.campus.common.auth.CurrentUserHolder.get().getId()
                : null;
        if (currentUserId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        if (orderId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单不存在");
        OrderMain order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单不存在");

        Long buyerId = order.getBuyerId();
        Long sellerId = order.getSellerId();
        if (!Objects.equals(currentUserId, buyerId) && !Objects.equals(currentUserId, sellerId)) {
            // 既不是买家也不是卖家，无权发起会话
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权发起会话");
        }
        return getOrCreateSession(buyerId, sellerId, order.getProductId()).getId();
    }

    @Override
    public ChatMessageDTO appendMessage(Long sessionId, Long fromUserId, String content) {
        if (sessionId == null || fromUserId == null || content == null || content.isBlank()) {
            return null;
        }
        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            return null;
        }
        if (!Objects.equals(session.getBuyerId(), fromUserId) && !Objects.equals(session.getSellerId(), fromUserId)) {
            // 非会话成员不能发消息
            return null;
        }

        Long toUserId = Objects.equals(session.getBuyerId(), fromUserId)
                ? session.getSellerId()
                : session.getBuyerId();

        ChatMessage msg = new ChatMessage();
        msg.setSessionId(sessionId);
        msg.setFromUserId(fromUserId);
        msg.setToUserId(toUserId);
        msg.setContent(content.trim());
        msg.setCreatedAt(LocalDateTime.now());
        msg = chatMessageRepository.save(msg);

        session.setLastMessage(msg.getContent());
        session.setLastTime(msg.getCreatedAt());
        if (Objects.equals(fromUserId, session.getBuyerId())) {
            Integer unread = session.getUnreadSeller() == null ? 0 : session.getUnreadSeller();
            session.setUnreadSeller(unread + 1);
        } else {
            Integer unread = session.getUnreadBuyer() == null ? 0 : session.getUnreadBuyer();
            session.setUnreadBuyer(unread + 1);
        }
        if (session.getUnreadBuyer() == null) session.setUnreadBuyer(0);
        if (session.getUnreadSeller() == null) session.setUnreadSeller(0);
        chatSessionRepository.save(session);

        return toMessageDto(msg);
    }

    private ChatSession getOrCreateSession(Long buyerId, Long sellerId, Long productId) {
        ChatSession existing = chatSessionRepository
                .findFirstByBuyerIdAndSellerIdAndProductId(buyerId, sellerId, productId);
        if (existing != null) {
            return existing;
        }
        ChatSession session = new ChatSession();
        session.setBuyerId(buyerId);
        session.setSellerId(sellerId);
        session.setProductId(productId);
        session.setLastMessage(null);
        session.setLastTime(LocalDateTime.now());
        session.setUnreadBuyer(0);
        session.setUnreadSeller(0);
        return chatSessionRepository.save(session);
    }

    private ChatSessionDTO toSessionDto(ChatSession s,
                                        Long currentUserId,
                                        Map<Long, Product> productMap,
                                        Map<Long, String> userNameMap) {
        ChatSessionDTO dto = new ChatSessionDTO();
        dto.setId(s.getId());
        dto.setProductId(s.getProductId());
        if (s.getProductId() != null && productMap.containsKey(s.getProductId())) {
            Product p = productMap.get(s.getProductId());
            dto.setProductTitle(p.getTitle());
            dto.setProductCoverUrl(p.getCoverUrl());
        }
        Long otherUserId = Objects.equals(s.getBuyerId(), currentUserId) ? s.getSellerId() : s.getBuyerId();
        dto.setOtherUserId(otherUserId);
        dto.setOtherNickname(userNameMap.getOrDefault(otherUserId, ""));
        dto.setLastMessage(s.getLastMessage());
        dto.setLastTime(DateTimes.format(s.getLastTime()));
        if (Objects.equals(s.getBuyerId(), currentUserId)) {
            dto.setUnreadCount(s.getUnreadBuyer());
        } else if (Objects.equals(s.getSellerId(), currentUserId)) {
            dto.setUnreadCount(s.getUnreadSeller());
        } else {
            dto.setUnreadCount(0);
        }
        return dto;
    }

    private ChatMessageDTO toMessageDto(ChatMessage m) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(m.getId());
        dto.setSessionId(m.getSessionId());
        dto.setFromUserId(m.getFromUserId());
        dto.setToUserId(m.getToUserId());
        dto.setContent(m.getContent());
        dto.setCreatedAt(DateTimes.format(m.getCreatedAt()));
        return dto;
    }
}

