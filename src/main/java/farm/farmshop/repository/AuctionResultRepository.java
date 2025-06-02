// AuctionResultRepository.java에 추가 메소드
package farm.farmshop.repository;

import farm.farmshop.dto.SellerAvgRating;
import farm.farmshop.entity.AuctionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionResultRepository extends JpaRepository<AuctionResult, Long> {

    // 낙찰자 ID로 조회
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.winningBid.member.id = :memberId ORDER BY ar.auctionEndTime DESC")
    List<AuctionResult> findByWinnerId(@Param("memberId") Long memberId);

    // 판매자 ID로 조회
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.product.member.id = :sellerId ORDER BY ar.auctionEndTime DESC")
    List<AuctionResult> findBySellerId(@Param("sellerId") Long sellerId);

    // 상품 ID로 조회
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.product.id = :productId")
    AuctionResult findByProductId(@Param("productId") Long productId);

    // 배송 상태별 조회
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.deliveryStatus = :status")
    List<AuctionResult> findByDeliveryStatus(@Param("status") String status);

    // 30일 전에 완료된 거래 조회 (개인정보 삭제용)
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.deliveryStatus = 'COMPLETED' AND ar.completedAt < :date")
    List<AuctionResult> findCompletedBefore(@Param("date") LocalDateTime date);

    // 진행 중인 거래 조회 (낙찰자 또는 판매자)
    @Query("SELECT ar FROM AuctionResult ar WHERE " +
            "(ar.winningBid.member.id = :memberId OR ar.product.member.id = :memberId) " +
            "AND ar.deliveryStatus != 'COMPLETED' " +
            "ORDER BY ar.auctionEndTime DESC")
    List<AuctionResult> findOngoingByMemberId(@Param("memberId") Long memberId);

    // 완료된 거래 조회 (낙찰자 또는 판매자)
    @Query("SELECT ar FROM AuctionResult ar WHERE " +
            "(ar.winningBid.member.id = :memberId OR ar.product.member.id = :memberId) " +
            "AND ar.deliveryStatus = 'COMPLETED' " +
            "ORDER BY ar.completedAt DESC")
    List<AuctionResult> findCompletedByMemberId(@Param("memberId") Long memberId);

    // 판매자가 올린 상품 중 후기가 남겨진 결과
    @Query("SELECT ar FROM AuctionResult ar WHERE ar.product.member.id = :sellerId  AND ar.review IS NOT NULL ORDER BY ar.completedAt DESC")
    List<AuctionResult> findBySellerIdAndReviewIsNotNull(@Param("sellerId") Long sellerId);

    // 특정 회원의 거래 통계 조회
    @Query("SELECT COUNT(ar) FROM AuctionResult ar WHERE ar.winningBid.member.id = :memberId AND ar.deliveryStatus = 'COMPLETED'")
    Long countCompletedWinningByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(ar) FROM AuctionResult ar WHERE ar.product.member.id = :memberId AND ar.deliveryStatus = 'COMPLETED'")
    Long countCompletedSellingByMemberId(@Param("memberId") Long memberId);

    // 1) 평점(rating)이 있는 거래 목록 조회
    @Query("SELECT ar FROM AuctionResult ar  WHERE ar.product.member.id = :sellerId AND ar.rating IS NOT NULL ORDER BY ar.completedAt DESC")
    List<AuctionResult> findByProduct_Member_IdAndRatingIsNotNull(@Param("sellerId") Long sellerId);


    // 2) 판매자 평균 평점 조회 (rating IS NOT NULL인 값들의 AVG)
    @Query("SELECT AVG(ar.rating) FROM AuctionResult ar  WHERE ar.product.member.id = :sellerId AND ar.rating IS NOT NULL")
    Double findAvgRatingBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT ar FROM AuctionResult ar WHERE ar.product.member.id = :sellerId AND ar.rating IS NOT NULL")
    List<AuctionResult> findRatingsBySeller(@Param("sellerId") Long sellerId);

    @Query(
      "SELECT ar.product.member.id AS sellerId, AVG(ar.rating) AS avgRating " +
      "FROM   AuctionResult ar " +
      "WHERE  ar.rating IS NOT NULL " +
      "GROUP  BY ar.product.member.id"
    )
    List<SellerAvgRating> findAvgRatingBySellerGroup();


}