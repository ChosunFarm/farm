package farm.farmshop.repository;

import farm.farmshop.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    // 상품별 입찰 내역 조회
    @Query("SELECT b FROM Bid b WHERE b.product.id = :productId ORDER BY b.bidAmount DESC")
    List<Bid> findByProductId(@Param("productId") Long productId);

    // 회원별 입찰 내역 조회
    @Query("SELECT b FROM Bid b WHERE b.member.id = :memberId ORDER BY b.bidTime DESC")
    List<Bid> findByMemberId(@Param("memberId") Long memberId);

    // 상품과 회원으로 입찰 내역 조회
    @Query("SELECT b FROM Bid b WHERE b.product.id = :productId AND b.member.id = :memberId")
    List<Bid> findByProductIdAndMemberId(@Param("productId") Long productId, @Param("memberId") Long memberId);

    // 상태별 입찰 내역 조회
    @Query("SELECT b FROM Bid b WHERE b.status = :status")
    List<Bid> findByStatus(@Param("status") String status);

    // 판매자의 상품에 대한 입찰 내역 조회
    @Query("SELECT b FROM Bid b WHERE b.product.member.id = :sellerId")
    List<Bid> findByProductSellerId(@Param("sellerId") Long sellerId);

    Optional<Bid> findTopByProductIdOrderByBidAmountDesc(Long productId);
}