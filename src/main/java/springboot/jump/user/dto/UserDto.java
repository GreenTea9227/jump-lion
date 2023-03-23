package springboot.jump.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import springboot.jump.user.UserRole;


@Getter
@Setter
@Builder
public class UserDto {

    @NotBlank(message = "username을 잘못 입력하였습니다.")
    private String username;

    @NotBlank(message = "email 잘못 입력하였습니다.")
    @Email(message = "email 형식이 올바르지 않습니다.")
    private String email;

    private UserRole role;
}
