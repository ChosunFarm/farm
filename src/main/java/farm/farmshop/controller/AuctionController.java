package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.*;
import farm.farmshop.entity.product.ProductImage;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;

@Controller
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * application.yml 에 설정된 업로드 루트 디렉터리 (예: "./uploads/images")  
     */
    @Value("${spring.file.upload.directory}")
    private String uploadRootDir;

    @GetMapping
    public String showAuctionForm(Model model, Principal principal) {
        if (principal != null) {
            Member member = memberRepository.findByEmail(principal.getName());
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }
        return "auction";
    }

    @PostMapping
    public String registerProduct(
            @RequestParam("category") String category,
            @RequestParam("name") String name,
            @RequestParam("price") int price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("gram") int gram,
            @RequestParam(value = "fruitName", required = false) String fruitName,
            @RequestParam(value = "vegetableName", required = false) String vegetableName,
            @RequestParam(value = "grainName", required = false) String grainName,
            @RequestParam(value = "description", required = false) String description, // 상품 설명 추가
            @RequestParam(value = "imageFile", required = false) MultipartFile[] imageFiles,
            Principal principal  // 현재 로그인한 사용자 정보 가져오기
    ) throws IOException {
        // 1) 상품 엔티티 생성
        Product product;
        switch (category) {
            case "F":
                Fruit fruit = new Fruit();
                fruit.setFruitName(fruitName);
                fruit.setGram(gram);
                product = fruit;
                break;
            case "V":
                Vegetable veg = new Vegetable();
                veg.setVegetableName(vegetableName);
                veg.setGram(gram);
                product = veg;
                break;
            case "G":
                Grain grain = new Grain();
                grain.setGrainName(grainName);
                grain.setGram(gram);
                product = grain;
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setStatus("pending"); // 상품 등록 시 기본 상태를 '검수 대기'로 설정
        product.setDescription(description); // 상품 설명 설정

        // 현재 로그인한 회원 정보 가져오기
        if (principal != null) {
            Member member = memberRepository.findByEmail(principal.getName());
            product.setMember(member); // 상품과 회원 연결
        }

        // 2) 저장 → ID 발급
        productService.saveProduct(product);

        // 3) 업로드된 파일들 처리 (원본 파일명 그대로 저장)
        if (imageFiles != null && imageFiles.length > 0) {
            for (MultipartFile file : imageFiles) {
                if (file.isEmpty()) continue;

                // 실제 디스크에 저장하고, _DB 에는 파일명만_
                String filename = saveImage(file);

                // PRODUCT_IMAGES 테이블에 URL 저장
                ProductImage pi = new ProductImage();
                pi.setProductId(product.getId());
                pi.setImageUrl(filename);
                productImageRepository.save(pi);

                // 대표 이미지가 비어있으면 첫 번째 것으로 설정
                if (product.getImageUrl() == null) {
                    product.setImageUrl(filename);
                    productService.saveProduct(product);
                }
            }
        }

        // 4) 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /**
     * 디스크에 파일을 저장하고, DB에는 **원본 파일명**을 반환합니다.
     */
    private String saveImage(MultipartFile imageFile) throws IOException {
        // 1) 업로드 폴더 절대경로 생성
        Path uploadPath = Paths.get(uploadRootDir).toAbsolutePath();
        Files.createDirectories(uploadPath);

        // 2) 원본 파일명 정리 (공백/.. 제거)
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());

        // 3) 디스크에 저장
        Path filePath = uploadPath.resolve(originalFilename);
        imageFile.transferTo(filePath.toFile());

        // 4) DB 에 저장할 값(원본 파일명) 리턴
        return originalFilename;
    }
}