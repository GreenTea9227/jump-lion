package springboot.jump.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.common.util.resolver.QuestionForm;
import springboot.jump.manytomany.QuestionSiteUser;
import springboot.jump.manytomany.QuestionSiteUserRepository;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserRepository;
import springboot.jump.user.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionServiceTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionSiteUserRepository questionSiteUserRepository;

    private String subject;
    private String content;

    @BeforeEach
    void before() {
        subject = "subject";
        content = "content";
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("10개 넣고 getList시 10개 반환 확인")
    void getList() {
        for (int i = 0; i < 10; i++) {
            Question question = Question.builder()
                    .subject(subject)
                    .content(content)
                    .build();
            questionRepository.save(question);
        }
        List<Question> list = questionService.getList();

        assertThat(list).size().isEqualTo(10);
    }

    @Test
    @DisplayName("id로 question 찾아오기")
    void getQuestion() {

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
    @DisplayName("create test")
    void create() {

        QuestionForm questionForm = new QuestionForm(subject, content);
        questionService.create(questionForm, null);

        Question findQuestion = questionRepository.findBySubject(subject);
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("page test")
    void pageGetList() {
        for (int i = 0; i < 40; i++) {
            Question question = Question.builder()
                    .subject(subject)
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

    @Test
    @DisplayName("page test")
    void modify() {
        //given

        QuestionForm questionForm = new QuestionForm(subject, content);
        questionService.create(questionForm, null);

        List<Question> all = questionRepository.findAll();
        Question question = all.get(0);

        //when
        String newContent = "new content";
        String newSubject = "new subject";
        QuestionForm newQuestionForm = new QuestionForm(newSubject, newContent);

        questionService.modify(question, newQuestionForm);

        //then
        Long id = question.getId();
        Question changedQuestion = questionService.getQuestion(id);
        assertThat(changedQuestion).isNotNull();
        assertThat(changedQuestion.getSubject()).isEqualTo(newSubject);
        assertThat(changedQuestion.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("delte test")
    void delete() {
        //given
        Question save = questionRepository.save(Question.builder()
                .subject(subject)
                .content(content)
                .build());
        //when
        questionService.delete(save);

        //then
        Optional<Question> findQuestion = questionRepository.findById(save.getId());

        assertThat(findQuestion).isEmpty();
    }

    @Test
    @DisplayName("추천을 누를시 저장이 제대로 되는지 확인")
    void vote() {
        //given
        SiteUser user = SiteUser.builder()
                .username("username")
                .password("1111")
                .role(UserRole.USER)
                .uuid("uuid1111")
                .build();
        userRepository.save(user);

        Question question = Question.builder()
                .content(content)
                .subject(subject)
                .build();
        questionRepository.save(question);

        //when
        questionService.vote(question.getId(), user);

        //then
        List<QuestionSiteUser> byQuestionId = questionSiteUserRepository.findByQuestionId(question.getId());
        QuestionSiteUser questionSiteUser = byQuestionId.get(0);

        assertThat(questionSiteUser.getQuestion()).isEqualTo(question);
        assertThat(questionSiteUser.getSiteUser()).isEqualTo(user);
    }
}