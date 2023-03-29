package springboot.jump.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Client {
    Google("google"), Naver("naver");
    private final String name;
}
