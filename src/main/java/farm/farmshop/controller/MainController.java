// package farm.farmshop.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// //import org.springframework.web.bind.annotation.RequestParam;


// @Controller

// public class MainController {

//     @RequestMapping("/")
//     public String main() {
//         return "main";
//     }

//     /*@GetMapping("/login")
//     public String login(){
//         return "login";
//     }*/
    
// }
package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberRepository memberRepository;

    @RequestMapping("/")
    public String main(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName(); // 로그인한 사용자의 이메일
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);  // ✅ 이거 추가!
            }
        } else {
            model.addAttribute("isLogin", false);     // ✅ 이거도 추가!
        }

        return "main"; // templates/main.html
    }

}
