package farm.farmshop.controller;

import farm.farmshop.dto.MemberEditDTO;
import farm.farmshop.entity.Member;
import farm.farmshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageEditController {

    private final MemberService memberService;

    // 회원정보 수정 페이지 이동
    @GetMapping("/edit-info")
    public String showEditInfoPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Member member = memberService.findByEmail(email);

        MemberEditDTO memberEditDTO = new MemberEditDTO();
        memberEditDTO.setUsername(member.getUsername());
        memberEditDTO.setEmail(member.getEmail());
        memberEditDTO.setPhone(member.getPhone());
        memberEditDTO.setAddress(member.getAddress());

        model.addAttribute("memberEditDTO", memberEditDTO);
        model.addAttribute("member", member);
        return "mypage/edit-info";
    }

    // 수정된 회원정보 처리
    @PostMapping("/update-info")
    public String updateMemberInfo(@ModelAttribute MemberEditDTO memberEditDTO,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        String email = userDetails.getUsername();

        boolean success = memberService.updateMemberInfo(email, memberEditDTO);

        if (!success) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "mypage/edit-info";
        }

        return "redirect:/mypage?updated=true";
    }
}
