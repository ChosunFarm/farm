package farm.farmshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;


@Controller

public class MainController {

    @RequestMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    
}
