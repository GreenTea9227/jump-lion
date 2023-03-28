package springboot.jump.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.user.dto.UserDto;
import springboot.jump.user.form.ChangePasswordForm;
import springboot.jump.user.form.UserFindPasswordForm;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String email;
    private String username;
    private String password;
    private String uuid;

    @BeforeEach
    void setArgument() {
        userRepository.deleteAll();
        email = "email2@naver.com";
        username = "username23";
        password = "1111";
        uuid = "uuid1234";
    }

    @Test
    @DisplayName("userService 저장 테스트")
    void create() {
        //given
        SiteUser siteUser = userService.create(username, email, password);

        //when
        Optional<SiteUser> siteUserOptional = userRepository.findById(siteUser.getId());

        //then
        assertThat(siteUserOptional).isNotEmpty();
        SiteUser user = siteUserOptional.get();
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches("1111", user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("11123123111", user.getPassword())).isFalse();
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("userService로 user 가져오기")
    void getUser() {

        SiteUser siteUser = userService.create(username, email, password);

        SiteUser findUser = userService.getUser(username);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getUsername()).isEqualTo(username);
        assertThat(findUser.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("userService로 가져올 시 userDto로 가져오기")
    void getUserDto() {

        userRepository.save(SiteUser.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(UserRole.USER)
                .build());

        UserDto userDto = userService.getUserDto(username);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getEmail()).isEqualTo(email);
        assertThat(userDto.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("email 값으로 가져오기")
    void findPasswordForm() {

        userRepository.save(SiteUser.builder()
                .email(email)
                .build());

        UserFindPasswordForm form = new UserFindPasswordForm();
        form.setEmail(email);

        String findEmail = userService.findPasswordForm(form);
        assertThat(findEmail).isEqualTo(email);
        assertThat(findEmail).isNotEqualTo("new@email");
    }

    @Test
    @DisplayName("uuid update test")
    void updateUuid() {

        userRepository.save(SiteUser.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(UserRole.USER)
                .build());

        userService.updateUuid(email, uuid);

        SiteUser byEmail = userRepository.findByEmail(email);
        assertThat(byEmail.getUuid()).isEqualTo(uuid);
        assertThat(byEmail.getUuid()).isNotEqualTo("no-uuid");
    }

    @Test
    @DisplayName("전송한 uuid가 같은 값인지 체크")
    void checkUuid() {

        userRepository.save(SiteUser.builder()
                        .username(username)
                .email(email)
                .uuid(uuid)
                .build());

        assertThat(userService.checkUuid(email, uuid)).isTrue();
        assertThat(userService.checkUuid(email, "falseuuid")).isFalse();
    }

    @Test
    @DisplayName("password change test")
    void changePwd() {
        //given
        userRepository.save(SiteUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .uuid(uuid)
                .build());

        //when
        String newPassword = "1234";

        ChangePasswordForm form = new ChangePasswordForm();
        form.setEmail(email);
        form.setPassword(newPassword);
        form.setCheckPassword(newPassword);
        userService.changePwd(form);

        //then
        SiteUser findUser = userRepository.findByEmail(email);
        assertThat(passwordEncoder.matches(newPassword, findUser.getPassword())).isTrue();
        assertThat(findUser.getPassword()).isNotEqualTo(password);
    }

    @Test
    @DisplayName("유저의 정보 chagne test")
    void changeInfo() {
        //given
        userRepository.save(SiteUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .uuid(uuid)
                .build());

        String newEmail = "newEmail@naver.com";
        String newUserName = "newUserName";

        UserDto userDto = UserDto.builder()
                .email(newEmail)
                .username(newUserName)
                .build();

        //when
        boolean change = userService.changeInfo(userDto, username);

        //then
        assertThat(change).isTrue();

        SiteUser user = userRepository.findByEmail(newEmail);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getUsername()).isEqualTo(newUserName);
    }
}