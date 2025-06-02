package farm.farmshop.entity;

import farm.farmshop.entity.product.Product;
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

    // Member.java에 추가할 부분
    @OneToMany(mappedBy = "member")
    private List<Product> products = new ArrayList<>();

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

    @Column(name = "intro")
    private String intro;

    @Column(name = "profile_image")
    private String profileImage;

}