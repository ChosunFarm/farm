package farm.farmshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // Foreign Key
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "deliver_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //==연관관계 메서드==//
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    /*public void addOrderProduct(OrderProduct orderProduct){
        orderProduct.add(orderProduct);
        orderProduct.setOrder(this);
    }*/

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
