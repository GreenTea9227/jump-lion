package springboot.jump.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserRepository;
import springboot.jump.aggregate.user.UserRole;

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
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //google

        ForOAuth2User oAuth2USer = null;

        if (registrationId.equals(OAuth2Client.Google.getName())) {
            oAuth2USer = new GoogleOAuth2User(attributes, registrationId);
        } else if (registrationId.equals(OAuth2Client.Naver.getName())) {
            oAuth2USer = new NaverOAuth2User(attributes, registrationId, "response");
            //TODO Naver 처리
        }

        return checkAndUpdate(oAuth2USer);
    }

    private PrincipalUser checkAndUpdate(ForOAuth2User googleOAuth2User) {

        Map<String, Object> attributes = googleOAuth2User.getAttributes();

        String name = String.valueOf(attributes.get("name"));
        String password = String.valueOf(attributes.get("sub")).substring(0, 8);
        String email = String.valueOf(attributes.get("email"));
        String picture = String.valueOf(attributes.get("picture"));

        SiteUser user = userRepository.findByEmail(email);

        String clientRegistrationName = googleOAuth2User.getClientRegistration();

        if (user == null) {
            user = SiteUser.builder()
                    .username(name + "_" + clientRegistrationName)
                    .password(passwordEncoder
                            .encode(password + UUID.randomUUID().toString().substring(0, 6)))
                    .role(UserRole.USER)
                    .email(email)
                    .picture(picture)
                    .build();
            userRepository.save(user);
        } else {
            checkChange(clientRegistrationName, name, email, picture, user);
        }

        return new PrincipalUser(user, attributes);
    }

    private static void checkChange(String registrationName, String name, String email, String picture, SiteUser findUser) {

        String checkEmail = findUser.getEmail();
        if (checkEmail == null || !checkEmail.equals(email))
            findUser.setEmail(email);
        String checkPicture = findUser.getPicture();
        if (checkPicture == null || !checkPicture.equals(picture))
            findUser.setPassword(picture);
        String checkName = findUser.getUsername();
        if (checkName == null || !checkName.equals(name))
            findUser.setUsername(name + "_" + registrationName);
    }
}
