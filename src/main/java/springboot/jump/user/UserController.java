package springboot.jump.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springboot.jump.user.dto.UserDto;
import springboot.jump.user.form.ChangePasswordForm;
import springboot.jump.user.form.UserCreateForm;
import springboot.jump.user.form.UserFindPasswordForm;
import springboot.jump.util.MailService;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {
    private final UserService userService;
    private final MailService mailService;

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

    @GetMapping("/findPassword")
    public String findPassword(UserFindPasswordForm userFindPasswordForm) {

        return "user/find_password";
    }

    @PostMapping("/findPassword")
    public String findPassword(@Validated UserFindPasswordForm userFindPasswordForm
            , BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "user/find_password";
        }

        String findEmail = userService.findPasswordForm(userFindPasswordForm);
        if (findEmail == null) {
            return "user/find_password";
        }

        //TODO mail 보내기
        String email = userFindPasswordForm.getEmail();
        String uuid = UUID.randomUUID().toString();

        userService.updateUuid(email, uuid);

        mailService.sendMail(email, uuid);
        HttpSession session = request.getSession();
        session.setAttribute("email", email);

        return "user/uuid_check";
    }

    @PostMapping("/uuidcheck")
    public String uuidCheck(@RequestParam String uuid, HttpServletRequest request,
                            Model model) {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (!userService.checkUuid(email, uuid)) {
            return "user/uuid_check";
        }

        //TODO uuid 인증 완료시 비밀번호 새로 정하기

        return "redirect:/user/changepwd";
    }

    @GetMapping("/changepwd")
    public String changePwd(ChangePasswordForm changePasswordForm, Model model
            , HttpServletRequest request, @SessionAttribute(required = false) String email) {

        if (email != null)
            changePasswordForm.setEmail(email);
        request.getSession().invalidate();

        return "user/change_password";
    }

    @PostMapping("/changepwd")
    public String changePwd(@Validated ChangePasswordForm changePasswordForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/user/change_password";
        }

        if (!changePasswordForm.getPassword().equals(changePasswordForm.getCheckPassword())) {
            bindingResult.reject("mismatchpwd", "비밀번호가 일치하지 않습니다.");
            return "/user/change_password";
        }

        userService.changePwd(changePasswordForm);

        return "redirect:/user/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/authenticated/changepwd")
    public String changePasswordAuth(ChangePasswordForm changePasswordForm) {
        //TODO 나중에 authentication 객체를 따로 만들어서 email 넣어주기
        return "/user/change_password";
    }

    //    @PreAuthorize("isAuthenticated() && #changePasswordForm.getEmail().equals(authentication.getName())")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/user/authenticated/changepwd")
    public String changePasswordAuth(@Validated ChangePasswordForm changePasswordForm, BindingResult bindingResult) {

        return "/user/change_password";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Model model, UsernamePasswordAuthenticationToken user) {
        UserDto userDto = userService.getUserDto(user.getName());
        model.addAttribute("userDto", userDto);
        return "user/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile")
    public String showProfile(@Validated UserDto userDto, BindingResult bindingResult, UsernamePasswordAuthenticationToken user) {

        if (!userService.changeInfo(userDto, user.getName())) {
            bindingResult.reject("err", "잘못된 값을 작성하였습니다. 다시 입력해주세요");
            return "user/profile";
        }

        SecurityContextHolder.getContext()
                .setAuthentication(createNewAuthentication(user, userDto.getUsername()));

        return "redirect:/user/profile";
    }

    private final UserSecurityService userSecurityService;

    private Authentication createNewAuthentication(Authentication authentication, String username) {
        UserDetails newPrincipal = userSecurityService.loadUserByUsername(username); //바뀐 값이 제대로 동작하는지 체크

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(newPrincipal, authentication.getCredentials(), newPrincipal.getAuthorities());

        newAuth.setDetails(authentication.getDetails());
        return newAuth;
    }
}
