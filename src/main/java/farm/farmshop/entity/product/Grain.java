package farm.farmshop.entity.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("G")
@Getter @Setter
public class Grain extends Product {
    private String grainName;
    // gram 필드는 상위 클래스(Product)로 이동
}