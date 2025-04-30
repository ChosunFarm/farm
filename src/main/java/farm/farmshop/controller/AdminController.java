package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @GetMapping("")
    public String adminMain(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 회원 목록 가져오기
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        // 상품 목록 가져오기
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        return "admin/dashboard";
    }

    @GetMapping("/members")
    public String memberManagement(
            @RequestParam(defaultValue = "1") int page,  // 페이지 번호 파라미터 추가
            @RequestParam(required = false) String search,  // 검색어 파라미터 추가
            @RequestParam(required = false) String memberType,  // 회원 유형 필터 추가
            @RequestParam(required = false) String status,  // 상태 필터 추가
            Model model,
            Principal principal) {

        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 페이지 크기 설정
        int pageSize = 10;

        // 회원 목록 가져오기 (검색 및 필터링 적용)
        // 실제 Repository 메서드는 기능에 맞게 구현 필요
        List<Member> allMembers;
        if (search != null && !search.isEmpty()) {
            // 검색어가 있는 경우 검색 조건에 맞는 회원 조회
            // ✅ 수정된 코드
            allMembers = memberRepository.findByUsernameOrEmailContaining(search);

        } else {
            // 검색어가 없는 경우 전체 회원 조회
            allMembers = memberRepository.findAll();
        }

        // 필터링 처리 (memberType, status에 따라)
        // 실제 구현은 Repository에 맞게 수정 필요
        if (memberType != null && !memberType.isEmpty()) {
            allMembers = allMembers.stream()
                    .filter(m -> memberType.equals(m.getUser_type()))
                    .toList();
        }

        if (status != null && !status.isEmpty()) {
            // status 필드가 있다면 필터링
            // 예: allMembers = allMembers.stream().filter(m -> status.equals(m.getStatus())).toList();
        }

        // 페이지네이션 처리
        int totalElements = allMembers.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        // 현재 페이지에 해당하는 회원만 추출
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalElements);

        List<Member> pagedMembers;
        if (start < totalElements) {
            pagedMembers = allMembers.subList(start, end);
        } else {
            pagedMembers = List.of(); // 빈 리스트
        }

        // 페이지 그룹핑
        int pageGroupSize = 5;
        int startPage = ((page - 1) / pageGroupSize) * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);
        if (endPage < 1) endPage = 1;

        // 모델에 데이터 추가
        model.addAttribute("members", pagedMembers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages > 0 ? totalPages : 1);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/members";
    }

    @GetMapping("/products")
    public String productManagement(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 상품 목록 가져오기
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        return "admin/products";
    }

    @PostMapping("/member/update-role")
    public String updateMemberRole(@RequestParam("memberId") Long memberId,
                                   @RequestParam("role") String role) {
        Member member = memberRepository.findOne(memberId);
        if (member != null) {
            member.setUser_type(role);
            memberRepository.save(member);
        }
        return "redirect:/admin/members";
    }
}