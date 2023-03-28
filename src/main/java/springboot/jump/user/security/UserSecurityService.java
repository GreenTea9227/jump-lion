package springboot.jump.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springboot.jump.oauth2.PrincipalUser;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<SiteUser> optionalSiteUser = userRepository.findByUsername(username);
        if (optionalSiteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        SiteUser siteUser = optionalSiteUser.get();
        return new PrincipalUser(siteUser);
    }
}
