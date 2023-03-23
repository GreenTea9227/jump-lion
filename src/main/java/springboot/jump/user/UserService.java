package springboot.jump.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.user.dto.UserDto;
import springboot.jump.user.form.ChangePasswordForm;
import springboot.jump.user.form.UserFindPasswordForm;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSecurityService securityService;

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

    public UserDto getUserDto(String username) {
        Optional<SiteUser> siteUser = userRepository.findByUsername(username);
        if (siteUser.isPresent()) {

            return  UserDto.builder()
                    .email(siteUser.get().getEmail())
                    .role(siteUser.get().getRole())
                    .username(siteUser.get().getUsername()).build();
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

    public boolean changeInfo(UserDto userDto, String username) {
        Optional<SiteUser> optionalSiteUser = userRepository.findByUsername(username);
        if (optionalSiteUser.isEmpty()) {
            return false;
        }

        SiteUser siteUser = optionalSiteUser.get();
        siteUser.setUsername(userDto.getUsername());
        siteUser.setEmail(userDto.getEmail());

        return true;
    }
}
