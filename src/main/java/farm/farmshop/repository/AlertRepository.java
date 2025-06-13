package farm.farmshop.repository;

import farm.farmshop.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // memberId 기준으로 읽지 않은 알림 조회
    List<Alert> findByMemberIdAndIsReadFalseOrderByCreatedAtDesc(Long memberId);

    // memberId 기준으로 전체 알림 조회
    List<Alert> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // (선택) 뱃지용 읽지 않은 알림 개수 조회
    long countByMemberIdAndIsReadFalse(Long memberId);

    void deleteByMemberIdAndIsReadTrue(Long memberId);
}
