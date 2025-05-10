package farm.farmshop.repository;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공

    // 회원 ID로 상품 조회
    @Query("SELECT p FROM Product p WHERE p.member.id = :memberId")
    List<Product> findByMemberId(@Param("memberId") Long memberId);

    // 상태별 상품 조회
    @Query("SELECT p FROM Product p WHERE p.status = :status")
    List<Product> findByStatus(@Param("status") String status);

    // 상태가 null인 상품 조회 (검수 신청 전 상품)
    @Query("SELECT p FROM Product p WHERE p.status IS NULL")
    List<Product> findByStatusNull();

    // 카테고리별 상품 조회
    @Query("SELECT p FROM Product p WHERE p.dtype = :dtype")
    List<Product> findByCategory(@Param("dtype") String dtype);

    // 카테고리 및 상태별 상품 조회
    @Query("SELECT p FROM Product p WHERE p.dtype = :dtype AND p.status = :status")
    List<Product> findByCategoryAndStatus(@Param("dtype") String dtype, @Param("status") String status);

    // 회원이 등록한 상품 목록 조회
    List<Product> findByMember(Member member);

}