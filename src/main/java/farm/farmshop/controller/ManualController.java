package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/manual")
public class ManualController {
    private final MemberRepository memberRepository;
    private final AlertService alertService;

    @GetMapping
    public String manualPage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);

                Long memberId = member.getId();   
                model.addAttribute("memberId", memberId);   

                // 미확인 알림 개수
                long unreadCnt = alertService.countUnread(memberId);
                model.addAttribute("alertCount", unreadCnt);

                // 미확인 알림 리스트 (DTO 변환)
                List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                                       .stream()
                                                       .map(AlertDto::fromEntity)
                                                       .toList();
                model.addAttribute("alertList", unreadList);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        return "manual"; // templates/manual.html
    }
}