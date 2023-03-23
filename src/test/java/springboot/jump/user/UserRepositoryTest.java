package springboot.jump.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername() {
        //givne
        String username = "user";
        String email = "email@naver.com";
        String password = "1111";
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
}