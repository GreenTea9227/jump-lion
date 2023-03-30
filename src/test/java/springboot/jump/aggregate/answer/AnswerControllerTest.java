package springboot.jump.aggregate.answer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.question.QuestionForm;
import springboot.jump.aggregate.question.QuestionRepository;
import springboot.jump.aggregate.question.QuestionService;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserRepository;
import springboot.jump.aggregate.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AnswerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    SiteUser user;
    Question question;

    @BeforeEach
    void setBefore() {
        userRepository.deleteAll();
        user = userService.create("user", "hello@naver.com", "1111");
        questionService.create(
                new QuestionForm("subject", "category", "content"), null);
        List<Question> questions = questionRepository.findAll();
        question = questions.get(0);
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 등록 성공 test")
    @Test
    void successCreateAnswer() throws Exception {

        ResultActions resultActions = mvc.perform(post("/answer/create/" + question.getId())
                        .param("content", "change content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Question findQuestion = questionRepository.findById(question.getId()).get();
        assertThat(findQuestion.getAnswers()).size().isEqualTo(1);
        assertThat(findQuestion.getAnswers().get(0).getContent()).isEqualTo("change content");

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("createAnswer"));
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 등록 실패 test")
    @Test
    void failCreateAnswer() throws Exception {

        mvc.perform(post("/answer/create/" + question.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/question_detail/" + question.getId()));
    }

    @Test
    void answerModify() {

    }

    @Test
    void testAnswerModify() {
    }

    @Test
    void delete() {
    }

    @Test
    void vote() {
    }
}