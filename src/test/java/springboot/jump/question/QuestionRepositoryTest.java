package springboot.jump.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    String content;
    String subject;

    @BeforeEach
    void setBefore() {
        content = "content";
        subject = "subject";
    }

    @Test
    @DisplayName("제목으로 찾기")
    void findBySubject() {
        //given

        Question question = Question.builder()
                .content(content)
                .subject(subject).build();

        questionRepository.save(question);

        //then
        Question findQuestion = questionRepository.findBySubject("subject");

        //then
        assertThat(findQuestion).isNotNull();
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("findBySubjectAndContent 실패 케이스")
    void findBySubjectAndContent() {
        //given

        Question question = Question.builder()
                .content(content)
                .subject(subject)
                .build();

        questionRepository.save(question);

        //then
        Question findQuestion = questionRepository.findBySubjectAndContent(subject, content);

        //then
        assertThat(findQuestion).isNotNull();
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("findBySubjectAndContent 잘못 입력시 null값 반환")
    void passFindBySubjectAndContent() {
        //given

        Question question = Question.builder()
                .content(content)
                .subject(subject)
                .build();

        questionRepository.save(question);
        Question no = questionRepository.findBySubjectAndContent(content, "no");

        assertThat(no).isNull();
    }

    @Test
    @DisplayName("like 연산")
    void findBySubjectLike() {
        //given

        Question question = Question.builder()
                .content(content)
                .subject(subject)
                .build();

        questionRepository.save(question);

        //then
        List<Question> subjectLike = questionRepository.findBySubjectLike("%sub%");

        //then
        assertThat(subjectLike).isNotEmpty();
        assertThat(subjectLike).containsOnly(question);
    }

    @Test
    @DisplayName("findAll 연산")
    void findAll() {
        //given
        String content1 = "content";
        String subject1 = "subject";
        Question question = Question.builder()
                .content(content1)
                .subject(subject1)
                .build();

        questionRepository.save(question);

        String content2 = "content";
        String subject2 = "subject";
        Question question2 = Question.builder()
                .content(content2)
                .subject(subject2)
                .build();

        questionRepository.save(question2);

        //then
        List<Question> all = questionRepository.findAll();

        //then
        assertThat(all).isNotEmpty();
        assertThat(all).containsExactly(question, question2);
    }

    @Test
    void findWithQuestionId() {
        Question question = Question.builder()
                .content(content)
                .subject(subject)
                .build();
        questionRepository.save(question);

        Question findQuestion = questionRepository.findWithQuestionId(question.getId());

        assertThat(findQuestion).isEqualTo(question);
    }
}