package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // 읽지 않은 alert 개수 조회 (초기 badge)
    @GetMapping("/count")
    public long getUnreadCount(@RequestParam Long memberId) {
        return alertService.countUnread(memberId);
    }

    // 전체 alert 리스트 조회
    @GetMapping("/all")
    public List<AlertDto> getAllAlerts(@RequestParam Long memberId) {
        return alertService.getAllAlerts(memberId).stream()
                            .map(AlertDto::fromEntity)
                            .toList();
    }

    // 읽지 않은 alert 리스트 조회 (초기 dropdown)
    @GetMapping("/unread")
    public List<AlertDto> getUnread(@RequestParam Long memberId) {
        return alertService.getUnreadAlerts(memberId).stream()
                           .map(AlertDto::fromEntity)
                           .toList();
    }

    // 전체 읽음 처리 (badge 0으로 리셋)
    @PostMapping("/mark-read")
    public void markAllRead(@RequestParam Long memberId) {
        alertService.markAllAsRead(memberId);
    }

    @DeleteMapping("/clear-read")
    public void clearRead(
        @RequestParam Long memberId
    ) {
        alertService.deleteReadAlerts(memberId);
    }
}
