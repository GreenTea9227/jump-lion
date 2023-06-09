package springboot.jump.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserRepository;
import springboot.jump.aggregate.user.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private String username = "user";
    private String email = "emailEmail@naver.com";
    private String password = "1111";

    @BeforeEach
    void setBefore() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername() {
        //given
        SiteUser user = SiteUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(UserRole.USER).build();

        //when
        userRepository.save(user);

        //then
        Optional<SiteUser> userOptional = userRepository.findByUsername(username);
        assertThat(userOptional).isNotEmpty();

        SiteUser siteUser = userOptional.get();
        assertThat(siteUser.getUsername()).isEqualTo(username);
        assertThat(siteUser.getEmail()).isEqualTo(email);
        assertThat(siteUser.getPassword()).isEqualTo(password);
    }

    @Test
    void findByEmail() {
        //givne

        SiteUser siteUser = SiteUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(UserRole.USER).build();

        //when
        userRepository.save(siteUser);

        SiteUser user = userRepository.findByEmail(email);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }
}