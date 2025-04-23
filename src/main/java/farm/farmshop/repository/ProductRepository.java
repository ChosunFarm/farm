package farm.farmshop.repository;

import farm.farmshop.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공
}