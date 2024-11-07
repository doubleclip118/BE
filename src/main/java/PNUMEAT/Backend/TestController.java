package PNUMEAT.Backend;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class TestController {

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if("access_token".equals(cookie.getName())){
                model.addAttribute("authToken", cookie.getValue());
            }
        }

        return "article/main";
    }

    @GetMapping("/backend")
    public String backend(){
        log.info("백엔드 리다이렉션 시키기");
        return "redirect:/backend-view";

    }

    @GetMapping("/backend-view")
    public String backendView() {
        return "backend";
    }
}
