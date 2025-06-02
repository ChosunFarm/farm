package farm.farmshop.repository;

import farm.farmshop.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdIn(List<Long> productIds);
    List<ProductImage> findByProductId(Long productId);
}