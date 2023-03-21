package springboot.jump.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String singup(UserCreateForm userCreateForm) {
        return "sign_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PostMapping("/signup")
    public String signup(@Validated UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign_form";
        }

        if (!userCreateForm.getPassword().equals(userCreateForm.getCheckPassword())) {
            bindingResult.rejectValue("checkPassword", "passwordIncorrect", "check비밀번호와 일치하지 않습니다.");
            return "sign_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "sign_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "sign_form";
        }

        return "redirect:/";
    }
}
