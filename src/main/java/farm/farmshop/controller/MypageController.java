package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.BidService;
import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;
    private final AlertService alertService;

    @PostMapping("/mypage/update")
    @Transactional
    public String updateProfile(@RequestParam("profileImage") MultipartFile image,
                                @RequestParam("introText") String intro,
                                Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        // 절대 경로 기준으로 저장
        if (!image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

            String uploadDir = System.getProperty("user.dir") + "/uploads/myage-profile/";
            Path savePath = Paths.get(uploadDir + filename);

            try {
                Files.createDirectories(savePath.getParent()); // 디렉토리 없으면 생성
                Files.copy(image.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
                member.setProfileImage(filename); // DB에 파일명 저장


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            member.setProfileImage("profile.png");
        }

        // 한줄 소개 저장
        member.setIntro(intro);

        // DB 저장
        memberRepository.save(member);

        return "redirect:/mypage";
    }

    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());

                String fullAddress = member.getAddress();
                String trimmedAddress = trimAddress(fullAddress);
                model.addAttribute("address", trimmedAddress);
                model.addAttribute("isLogin", true);

                // model.addAttribute("profileImage", member.getProfileImage());
                String profileImage = member.getProfileImage();
                if (profileImage == null || profileImage.isBlank() || "null".equals(profileImage)) {
                    model.addAttribute("profileImage", null);  // 강제로 null 처리
                } else {
                    model.addAttribute("profileImage", profileImage);
                }

                model.addAttribute("intro", member.getIntro());

                Long memberId = member.getId();
                model.addAttribute("memberId", memberId);         
                long unreadCnt = alertService.countUnread(memberId);
                model.addAttribute("alertCount", unreadCnt);

                List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                                    .stream()
                                                    .map(AlertDto::fromEntity)
                                                    .toList();
                model.addAttribute("alertList", unreadList);

            }
        } else {
            model.addAttribute("isLogin", false);
        }

        return "mypage";

    }

    private String trimAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isBlank()) return "";

        String[] parts = fullAddress.trim().split("\\s+");
        int end = Math.min(parts.length, 3); // 앞 세 단어까지만
        return String.join(" ", Arrays.copyOfRange(parts, 0, end));
    }

    @GetMapping("/mypage/view-info")
    public String viewInfo(Model model, Principal principal) {
        String email = principal.getName();

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return "redirect:/login";
        }

        Long memberId = member.getId();
        model.addAttribute("memberId", memberId);         

        long unreadCnt = alertService.countUnread(memberId);
        model.addAttribute("alertCount", unreadCnt);

        List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                               .stream()
                                               .map(AlertDto::fromEntity)
                                               .toList();
        model.addAttribute("alertList", unreadList);
        model.addAttribute("member", member);
        return "mypage/view-info";
    }
}