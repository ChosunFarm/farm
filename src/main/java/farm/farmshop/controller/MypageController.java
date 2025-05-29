package farm.farmshop.controller;

import farm.farmshop.dto.MyBidDTO;
import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;

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

                // ✅ 로그 출력 (터미널 확인용)
                System.out.println("✅ 프로필 이미지 저장됨: " + savePath.toAbsolutePath());
                System.out.println("✅ 접근 경로: /uploads/profile/" + filename);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 한줄 소개 저장
        member.setIntro(intro);

        // DB 저장
        memberRepository.save(member);

        return "redirect:/mypage";
    }

    // 회원정보 수정
    @GetMapping("/mypage/edit-info")
    public String showEditInfoForm(Model model, Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        model.addAttribute("member", member);
        return "mypage/my-editInfo";
    }

    @PostMapping("/mypage/edit-info")
    @Transactional
    public String updateMemberInfo(@ModelAttribute Member updatedMember, Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        member.setUsername(updatedMember.getUsername());
        member.setPhone(updatedMember.getPhone());
        member.setAddress(updatedMember.getAddress());

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

                model.addAttribute("profileImage", member.getProfileImage());
                model.addAttribute("intro", member.getIntro());

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
}