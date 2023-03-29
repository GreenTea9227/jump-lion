package springboot.jump.answer;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.answer.AnswerRepository;
import springboot.jump.aggregate.question.QuestionRepository;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserRepository;
import springboot.jump.aggregate.voter.AnswerVoter;
import springboot.jump.aggregate.voter.AnswerVoterRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AnswerVoterRepositoryTest {

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    AnswerVoterRepository answerVoterRepository;

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

            AnswerVoter answerVoter = AnswerVoter.builder()
                    .answer(answer)
                    .siteUser(siteUser)
                    .build();
            answerVoterRepository.save(answerVoter);
        }

        List<AnswerVoter> list = answerVoterRepository.findByAnswerId(answer.getId());
        AnswerVoter answerVoter = list.get(0);
        assertThat(emf.getPersistenceUnitUtil().isLoaded(answerVoter.getSiteUser())).isTrue();
        assertThat(emf.getPersistenceUnitUtil().isLoaded(answerVoter.getAnswer())).isTrue();
        assertThat(list).size().isEqualTo(15);
    }
}