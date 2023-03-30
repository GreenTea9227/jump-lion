package springboot.jump.answer;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.answer.AnswerRepository;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.question.QuestionRepository;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserRepository;
import springboot.jump.aggregate.voter.AnswerVoter;
import springboot.jump.aggregate.voter.AnswerVoterRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AnswerRepositoryTest {

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    AnswerVoterRepository answerVoterRepository;

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

            AnswerVoter answerVoter = AnswerVoter.builder()
                    .answer(answer1)
                    .siteUser(siteUser)
                    .build();
            answerVoterRepository.save(answerVoter);
            answer1.getVoter().add(answerVoter);
        }

        for (int i = 10; i < 20; i++) {
            SiteUser siteUser = SiteUser.builder()
                    .email(email + i)
                    .username(username + i)
                    .build();
            userRepository.save(siteUser);

            AnswerVoter answerVoter = AnswerVoter.builder()
                    .answer(answer2)
                    .siteUser(siteUser)
                    .build();
            answerVoterRepository.save(answerVoter);
            answer2.getVoter().add(answerVoter);
        }

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Answer> answers = answerRepository.findWithQuestionId(question.getId(), pageRequest);

        assertThat(answers.getContent()).size().isEqualTo(2);

        List<Answer> contents = answers.getContent();
        assertThat(contents.get(0).getVoter()).size().isEqualTo(10);
        assertThat(contents.get(1).getVoter()).size().isEqualTo(5);
    }

    @Test
    void findByAuthor_Id() {

        //given
        SiteUser siteUser = SiteUser.builder()
                .email(email)
                .username(username)
                .build();
        userRepository.save(siteUser);

        Answer answer = Answer.builder()
                .content(content)
                .author(siteUser)
                .build();
        answerRepository.save(answer);

        //when
        List<Answer> findAuthorList = answerRepository.findByAuthor_Id(siteUser.getId());

        //then
        assertThat(findAuthorList).size().isEqualTo(1);
        assertThat(findAuthorList.get(0)).isEqualTo(answer);
    }
}
