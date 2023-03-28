package springboot.jump.answer;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.manytomany.AnswerSiteUser;
import springboot.jump.manytomany.AnswerSiteUserRepository;
import springboot.jump.question.QuestionRepository;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AnswerSiteUserRepositoryTest {

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    AnswerSiteUserRepository answerSiteUserRepository;

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManagerFactory emf;

    private String content;
    private String subject;
    private String email;
    private String username;

    @BeforeEach
    void setBefore() {
        content = "content";
        subject = "subject";
        email = "email";
        username = "username";
    }

    @Test
    void findWithQuestionId() {

        Answer answer = Answer.builder()
                .content(content)
                .build();
        answerRepository.save(answer);

        for (int i = 0; i < 15; i++) {
            SiteUser siteUser = SiteUser.builder()
                    .email(email + i)
                    .username(username + i)
                    .build();
            userRepository.save(siteUser);

            answer.setAuthor(siteUser);

            AnswerSiteUser answerSiteUser = AnswerSiteUser.builder()
                    .answer(answer)
                    .siteUser(siteUser)
                    .build();
            answerSiteUserRepository.save(answerSiteUser);
        }

        List<AnswerSiteUser> list = answerSiteUserRepository.findByAnswerId(answer.getId());
        AnswerSiteUser answerSiteUser = list.get(0);
        assertThat(emf.getPersistenceUnitUtil().isLoaded(answerSiteUser.getSiteUser())).isTrue();
        assertThat(emf.getPersistenceUnitUtil().isLoaded(answerSiteUser.getAnswer())).isTrue();
        assertThat(list).size().isEqualTo(15);
    }
}