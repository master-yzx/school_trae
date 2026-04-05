package com.campus.modules.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_main")
public class OrderMain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 64)
    private String orderNo;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_title", nullable = false, length = 200)
    private String productTitle;

    @Column(name = "product_cover_url", length = 255)
    private String productCoverUrl;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "delivery_type_text", length = 50)
    private String deliveryTypeText;

    @Column(length = 255)
    private String address;

    @Column(name = "logistics_no", length = 64)
    private String logisticsNo;

  @Column(name = "payment_method", length = 30)
  private String paymentMethod;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  /**
   * 结算分组 ID，同一次购物车结算的多张卖家订单会共享同一个 groupId。
   * 普通单品下单时为空。
   */
  @Column(name = "group_id", length = 64)
  private String groupId;
}

