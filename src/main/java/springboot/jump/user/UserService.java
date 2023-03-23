package springboot.jump.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setPassword(passwordEncoder.encode(password));
        siteUser.setRole(UserRole.USER);
        userRepository.save(siteUser);
        return siteUser;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public String findPasswordForm(UserFindPasswordForm userFindPasswordForm) {
        SiteUser user = userRepository.findByEmail(userFindPasswordForm.getEmail());
        if (user == null) {
            return null;
        }
        return user.getEmail();
    }

    public void updateUuid(String email, String uuid) {
        SiteUser siteUser = userRepository.findByEmail(email);
        siteUser.setUuid(uuid);
    }

    public boolean checkUuid(String email, String uuid) {
        SiteUser user = userRepository.findByEmail(email);

        return user.getUuid().equals(uuid);
    }

    public void changePwd(ChangePasswordForm changePasswordForm) {
        SiteUser siteUser = userRepository.findByEmail(changePasswordForm.getEmail());
        if (siteUser == null)
            throw new DataNotFoundException("데이터 없음");

        siteUser.setPassword(passwordEncoder.encode(changePasswordForm.getPassword()));
    }
}
