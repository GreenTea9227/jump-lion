package springboot.jump.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFindPasswordForm {

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email
    private String email;
}
