package springboot.jump.answer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.answer.AnswerRepository;
import springboot.jump.aggregate.answer.AnswerService;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.question.QuestionRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    void deleteAll() {
        answerRepository.deleteAll();
    }

    @Test
    @DisplayName("Answer 객체 저장")
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
        answerService.create(question, "change", null);

        //then
        List<Answer> all = answerRepository.findAll();
        assertThat(all).isNotEmpty();
        Answer answer = all.get(0);

        assertThat(answer.getContent()).isEqualTo("change");
        assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("Answer 객체 가져오기")
    void getAnswer() {
        //given
        Answer answer = Answer.builder()
                .content("content").build();

        //when
        answerRepository.save(answer);
        Answer findAnswer = answerService.getAnswer(answer.getId());

        //then
        assertThat(findAnswer).isNotNull();
        assertThat(findAnswer.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("Answer 객체 수정")
    void modify() {
        //given
        String content = "content";
        String subject = "subject";
        Question question = Question.builder()
                .content(content)
                .subject(subject).build();
        questionRepository.save(question);

        Answer answer = Answer.builder()
                .question(question)
                .content("content")
                .build();
        answerRepository.save(answer);

        //when
        Answer findAnswer = answerService.getAnswer(answer.getId());

        String newContent = "new content";
        answerService.modify(findAnswer, newContent);

        Answer changedAnswer = answerService.getAnswer(answer.getId());

        //then
        assertThat(changedAnswer).isNotNull();
        assertThat(changedAnswer.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("Answer 객체 삭제")
    void delete() {
        //given
        Answer answer = Answer.builder()
                .content("content")
                .build();
        answerRepository.save(answer);

        //when
        answerService.delete(answer.getId());

        //then
        Optional<Answer> findAnswer = answerRepository.findById(answer.getId());

        assertThat(findAnswer).isEmpty();
    }
}