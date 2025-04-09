package farm.farmshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String username;

    private String phone;

    /*@Embedded
    private Address address;*/
    private String address;

    @Column(name = "user_type")
    private String user_type;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}