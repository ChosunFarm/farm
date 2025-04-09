package farm.farmshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auction")  // 클래스 레벨에서 경매 관련 경로를 지정합니다.
public class AuctionController {

    // GET /auction
    @GetMapping
    public String auctionPage() {
        return "auction";  // src/main/resources/templates/auction.html 템플릿 반환
    }
}
