package springboot.jump.aggregate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.answer.AnswerRepository;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.question.QuestionRepository;
import springboot.jump.aggregate.user.dto.UserDto;
import springboot.jump.aggregate.user.form.ChangePasswordForm;
import springboot.jump.aggregate.user.form.UserFindPasswordForm;
import springboot.jump.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
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

            SiteUser findUser = siteUser.get();
            List<Answer> answerList = answerRepository.findByAuthor_Id(findUser.getId());
            List<Question> questionList = questionRepository.findByAuthor_Id(findUser.getId());
            return UserDto.builder()
                    .email(findUser.getEmail())
                    .role(findUser.getRole())
                    .username(findUser.getUsername())
                    .answers(answerList)
                    .questions(questionList)
                    .build();
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

    @Transactional
    public void updateUuid(String email, String uuid) {
        SiteUser siteUser = userRepository.findByEmail(email);
        siteUser.setUuid(uuid);
    }

    public boolean checkUuid(String email, String uuid) {
        SiteUser user = userRepository.findByEmail(email);

        return user.getUuid().equals(uuid);
    }

    @Transactional
    public void changePwd(ChangePasswordForm changePasswordForm) {
        SiteUser siteUser = userRepository.findByEmail(changePasswordForm.getEmail());
        if (siteUser == null)
            throw new DataNotFoundException("데이터 없음");

        siteUser.setPassword(passwordEncoder.encode(changePasswordForm.getPassword()));
    }

    @Transactional
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
