package springboot.jump.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserRepository;
import springboot.jump.user.UserRole;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        //Google
        Map<String, Object> map = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //google

        String password = String.valueOf(map.get("sub")).substring(0, 8);
        //TODO Naver 처리

        String name = String.valueOf(map.get("name"));
        String email = String.valueOf(map.get("email"));
        String picture = String.valueOf(map.get("picture"));

        SiteUser findUser = userRepository.findByEmail(email);

        if (findUser == null) {
            userRepository.save(SiteUser.builder()
                    .username(name)
                    .password(passwordEncoder.encode(password + UUID.randomUUID().toString().substring(0, 6)))
                    .role(UserRole.USER)
                    .email(email)
                    .picture(picture)
                    .build());
        }

        //TODO 정보 변경시 change 메소드 필요
        return new PrincipalUser(findUser, map);
    }
}
