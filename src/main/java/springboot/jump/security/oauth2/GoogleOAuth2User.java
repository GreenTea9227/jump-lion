package springboot.jump.security.oauth2;

import java.util.Map;

public class GoogleOAuth2User extends ForOAuth2User {

    public GoogleOAuth2User(Map<String, Object> attributes, String clientRegistration) {
        super(attributes, clientRegistration);
    }
}
