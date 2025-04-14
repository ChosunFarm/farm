package farm.farmshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Live_AuctionController {
    
    @GetMapping("/live_auction")
    public String showLiveAuctionPage() {
        return "live_auction"; // templates/live_auction.html 을 렌더링
    }
}
