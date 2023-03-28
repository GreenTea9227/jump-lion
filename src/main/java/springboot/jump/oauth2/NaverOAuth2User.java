package springboot.jump.oauth2;

import java.util.Map;

public class NaverOAuth2User extends ForOAuth2User {
    public NaverOAuth2User(Map<String, Object> attributes, String clientRegistration, String key) {
        super(attributes, clientRegistration, key);
    }
}
