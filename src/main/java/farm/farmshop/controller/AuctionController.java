package farm.farmshop.controller;

import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;

    // 상품 등록 폼 페이지 보여주기
    @GetMapping
    public String showAuctionForm(Model model, HttpSession session) {
        // 로그인 상태 확인
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        String username = (String) session.getAttribute("username");

        model.addAttribute("isLogin", isLogin != null ? isLogin : false);
        model.addAttribute("username", username);

        return "auction";
    }

    // 상품 등록 처리
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
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        // 카테고리에 따라 적절한 상품 객체 생성
        Product product = null;

        switch (category) {
            case "F":
                Fruit fruit = new Fruit();
                fruit.setFruitName(fruitName);
                fruit.setGram(gram);
                product = fruit;
                break;
            case "V":
                Vegetable vegetable = new Vegetable();
                vegetable.setVegetableName(vegetableName);
                vegetable.setGram(gram);
                product = vegetable;
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

        // 공통 필드 설정
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);

        // 이미지 저장 (이미지 파일이 존재하는 경우)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = saveImage(imageFile);  // 이미지 저장 후 URL 얻기
                product.setImageUrl(imageUrl);  // 이미지 URL을 product 객체에 설정
            } catch (IOException e) {
                e.printStackTrace();  // 예외 처리
            }
        }

        // 상품 저장
        productService.saveProduct(product);

        return "redirect:/"; // 메인 페이지로 리다이렉트
    }


    public String saveImage(MultipartFile imageFile) throws IOException {
        // 실제 경로로 저장할 경로 (static/images 디렉토리)
        String uploadDir = Paths.get("src", "main", "resources", "static", "images").toString(); // 프로젝트 내 static/images 디렉토리 사용

        // 이미지 파일 이름 (원본 파일 이름을 그대로 사용)
        String imageName = imageFile.getOriginalFilename();

        // 저장할 경로
        Path path = Paths.get(uploadDir, imageName);

        // 디렉터리 생성 (존재하지 않으면 생성)
        Files.createDirectories(path.getParent()); // 디렉터리 생성
        imageFile.transferTo(path.toFile()); // 파일 저장

        // 저장된 이미지 파일 경로 반환 (웹에서 접근 가능한 경로)
        return "/images/" + imageName; // 반환 경로
    }
}
