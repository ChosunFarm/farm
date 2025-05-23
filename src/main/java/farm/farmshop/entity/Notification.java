package farm.farmshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
public class Notification {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "type")
    private String type; // BID_ACCEPTED, TRANSACTION_INFO, DELIVERY_STATUS, etc.

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "related_id")
    private Long relatedId; // AuctionResult ID 등

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}