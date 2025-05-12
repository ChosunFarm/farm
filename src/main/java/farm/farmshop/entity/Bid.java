package farm.farmshop.entity;

import farm.farmshop.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Getter @Setter
public class Bid {

    @Id @GeneratedValue
    @Column(name = "bid_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "bid_amount")
    private Integer bidAmount;

    @Column(name = "bid_time")
    private LocalDateTime bidTime;

    @Column(name = "status")
    private String status; // pending, accepted, rejected
}