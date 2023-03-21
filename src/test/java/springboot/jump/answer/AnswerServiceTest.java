package springboot.jump.answer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.question.Question;
import springboot.jump.question.QuestionRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnswerServiceTest {

    @Autowired
    AnswerService answerService;
    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    QuestionRepository questionRepository;

    @AfterEach
    void delete() {
        answerRepository.deleteAll();
    }

    @Test
    void create() {
        //given
        String content = "content";
        String subject = "subject";
        Question question = Question.builder()
                .content(content)
                .subject(subject).build();
        questionRepository.save(question);
        System.out.println(question);

        //when
        answerService.create(question,"change",null);

        //then
        List<Answer> all = answerRepository.findAll();
        assertThat(all).isNotEmpty();
        Answer answer = all.get(0);

        assertThat(answer.getContent()).isEqualTo("change");
        assertThat(answer.getQuestion()).isEqualTo(question);
    }
}