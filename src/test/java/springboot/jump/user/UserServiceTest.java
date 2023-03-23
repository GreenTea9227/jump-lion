package springboot.jump.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void create() {
        //given
        SiteUser siteUser = userService.create("username", "email", "1111");

        //when
        Optional<SiteUser> siteUserOptional = userRepository.findById(siteUser.getId());

        //then
        assertThat(siteUserOptional).isNotEmpty();
        SiteUser user = siteUserOptional.get();
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getEmail()).isEqualTo("email");
        assertThat(passwordEncoder.matches("1111", user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("11123123111", user.getPassword())).isFalse();
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void getUser() {
        String username = "username";
        String email = "email";
        String password = "1111";
        SiteUser siteUser = userService.create(username, email, password);

        SiteUser findUser = userService.getUser(username);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getUsername()).isEqualTo(username);
        assertThat(findUser.getEmail()).isEqualTo(email);
    }
}