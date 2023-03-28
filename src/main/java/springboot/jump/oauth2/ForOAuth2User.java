package springboot.jump.oauth2;

import lombok.Getter;

import java.util.Map;


@Getter
public abstract class ForOAuth2User {

    private Map<String, Object> attributes;
    private String key;
    private String clientRegistration;

    //naver
    public ForOAuth2User(Map<String, Object> attributes, String key, String clientRegistration){
        this.attributes = (Map<String, Object>) attributes.get(key);
        this.key = key;
        this.clientRegistration = clientRegistration;
    }


    //google
    public ForOAuth2User(Map<String, Object> attributes, String clientRegistration) {
        this.attributes = attributes;
        this.clientRegistration = clientRegistration;
    }

}
