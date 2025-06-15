package farm.farmshop.service;

import farm.farmshop.entity.Alert;
import farm.farmshop.dto.AlertDto;
import farm.farmshop.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // memberId로 변경
    public Alert sendAlert(Long memberId, Long productId, String type, String message) {
        Alert alert = Alert.builder()
                .memberId(memberId)
                .productId(productId)
                .type(type)
                .message(message)
                .isRead(false)
                .build();
        Alert saved = alertRepository.save(alert);

        // WebSocket 푸시도 memberId 사용
        messagingTemplate.convertAndSend(
            "/topic/alerts/" + memberId,
            new AlertDto(saved.getId(), saved.getType(), saved.getMessage(), saved.getCreatedAt())
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Alert> getUnreadAlerts(Long memberId) {
        return alertRepository.findByMemberIdAndIsReadFalseOrderByCreatedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public List<Alert> getAllAlerts(Long memberId) {
        return alertRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional
    public void markAllAsRead(Long memberId) {
        List<Alert> alerts = alertRepository.findByMemberIdAndIsReadFalseOrderByCreatedAtDesc(memberId);
        for (Alert a : alerts) {
            a.setIsRead(true);
        }
        alertRepository.saveAll(alerts);
    }

    @Transactional
    public void markAsRead(Long alertId) {
        alertRepository.findById(alertId).ifPresent(a -> {
            a.setIsRead(true);
            alertRepository.save(a);
        });
    }

    @Transactional
    public void deleteReadAlerts(Long memberId) {
        alertRepository.deleteByMemberIdAndIsReadTrue(memberId);
    }

    public long countUnread(Long memberId) {
        return alertRepository.countByMemberIdAndIsReadFalse(memberId);
    }
}
