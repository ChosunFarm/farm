package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String memberType,
            @RequestParam(required = false) String status,
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
        List<Member> allMembers;
        if (search != null && !search.isEmpty()) {
            // 검색어가 있는 경우 검색 조건에 맞는 회원 조회
            allMembers = memberRepository.findByUsernameOrEmailContaining(search);
        } else {
            // 검색어가 없는 경우 전체 회원 조회
            allMembers = memberRepository.findAll();
        }

        // 필터링 처리 (memberType, status에 따라)
        if (memberType != null && !memberType.isEmpty()) {
            allMembers = allMembers.stream()
                    .filter(m -> memberType.equals(m.getUser_type()))
                    .toList();
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

    @GetMapping("/pendinglist")
    public String pendingProducts(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);

                // 관리자 권한 확인
                if (!"ADMIN".equals(member.getUser_type())) {
                    return "redirect:/"; // 관리자가 아닌 경우 메인 페이지로 리다이렉트
                }
            }
        } else {
            model.addAttribute("isLogin", false);
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 검수 대기 중인 상품만 가져오기 (status가 'pending'인 상품)
        List<Product> pendingProducts = productService.findByStatus("pending");
        model.addAttribute("pendingProducts", pendingProducts);

        return "admin/pendinglist";
    }
    @PostMapping("/products/approve")
    public String approveProduct(@RequestParam("productId") Long productId,
                                 RedirectAttributes redirectAttributes) {
        // 상품 검수 승인 처리
        productService.approveProduct(productId);

        redirectAttributes.addFlashAttribute("message", "상품 검수가 승인되었습니다.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        // 실시간 경매 페이지로 리다이렉트
        return "redirect:/live_auction";
    }

    @PostMapping("/products/reject")
    public String rejectProduct(@RequestParam("productId") Long productId,
                                RedirectAttributes redirectAttributes) {
        // 상품 검수 거부 처리
        productService.rejectProduct(productId);

        redirectAttributes.addFlashAttribute("message", "상품 검수가 거부되었습니다.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        // 검수 대기 목록 페이지로 리다이렉트
        return "redirect:/admin/pendinglist";
    }
}