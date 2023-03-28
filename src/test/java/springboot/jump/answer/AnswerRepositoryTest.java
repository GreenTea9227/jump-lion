package springboot.jump.answer;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.manytomany.AnswerSiteUser;
import springboot.jump.manytomany.AnswerSiteUserRepository;
import springboot.jump.question.Question;
import springboot.jump.question.QuestionRepository;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AnswerRepositoryTest {

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    AnswerSiteUserRepository answerSiteUserRepository;

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

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

        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .build();
        questionRepository.save(question);

        Answer answer1 = Answer.builder()
                .content(content)
                .question(question)
                .build();
        answerRepository.save(answer1);

        Answer answer2 = Answer.builder()
                .content(content)
                .question(question)
                .build();
        answerRepository.save(answer2);

        for (int i = 0; i < 5; i++) {
            SiteUser siteUser = SiteUser.builder()
                    .email(email + i)
                    .username(username + i)
                    .build();
            userRepository.save(siteUser);

            AnswerSiteUser answerSiteUser = AnswerSiteUser.builder()
                    .answer(answer1)
                    .siteUser(siteUser)
                    .build();
            answerSiteUserRepository.save(answerSiteUser);
            answer1.getVoter().add(answerSiteUser);
        }

        for (int i = 10; i < 20; i++) {
            SiteUser siteUser = SiteUser.builder()
                    .email(email + i)
                    .username(username + i)
                    .build();
            userRepository.save(siteUser);

            AnswerSiteUser answerSiteUser = AnswerSiteUser.builder()
                    .answer(answer2)
                    .siteUser(siteUser)
                    .build();
            answerSiteUserRepository.save(answerSiteUser);
            answer2.getVoter().add(answerSiteUser);
        }

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Answer> answers = answerRepository.findWithQuestionId(question.getId(), pageRequest);

        assertThat(answers.getContent()).size().isEqualTo(2);

        List<Answer> contents = answers.getContent();
        assertThat(contents.get(0).getVoter()).size().isEqualTo(10);
        assertThat(contents.get(1).getVoter()).size().isEqualTo(5);
    }
}
