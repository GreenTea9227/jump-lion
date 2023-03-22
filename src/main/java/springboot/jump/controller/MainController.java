package springboot.jump.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/question/list";
    }

    @ResponseBody
    @GetMapping("/denied")
    public String denied() {
        return "denied!!!!!";
    }
}
