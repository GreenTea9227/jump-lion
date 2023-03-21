package springboot.jump.question;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.resolver.QuestionForm;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionServiceTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;


    @Test
    @DisplayName("10개 넣고 getList시 10개 반환 확인")
    void getList() {
        for (int i = 0; i < 10; i++) {
            Question question = Question.builder()
                    .subject("subject")
                    .content("content")
                    .build();
            questionRepository.save(question);
        }
        List<Question> list = questionService.getList();

        assertThat(list).size().isEqualTo(10);
    }

    @Test
    @DisplayName("id로 question 찾아오기")
    void getQuestion() {
        String subject = "subject";
        String content = "content";
        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .build();
        questionRepository.save(question);

        Question findQuestion = questionService.getQuestion(question.getId());
        assertThat(findQuestion).isEqualTo(question);
        assertThat(findQuestion.getSubject()).isEqualTo(subject);
        assertThat(findQuestion.getContent()).isEqualTo(content);
    }

    @Test
    void create() {
        String subject = "subject";
        String content = "content";

        QuestionForm questionForm = new QuestionForm(subject,content);
        questionService.create(questionForm,null);

        Question findQuestion = questionRepository.findBySubject(subject);
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("page test")
    void pageGetList() {
        for (int i = 0; i < 40; i++) {
            Question question = Question.builder()
                    .subject("subject")
                    .content("content")
                    .build();
            questionRepository.save(question);
        }
        Page<Question> page = questionService.getList(2);

        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.hasPrevious()).isTrue();
    }


}