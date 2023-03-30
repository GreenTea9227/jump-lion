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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private AnswerRepository answerRepository;

    SiteUser user;
    Question question;
    Answer answer;

    @BeforeEach
    void setBefore() {
        userRepository.deleteAll();
        questionRepository.deleteAll();
        answerRepository.deleteAll();

        user = userService.create("user", "hello@naver.com", "1111");

        questionService.create(
                new QuestionForm("subject", "category", "content"), null);

        List<Question> questions = questionRepository.findAll();
        question = questions.get(0);
        answer = answerRepository.save(Answer.builder()
                .content("content")
                .author(user)
                .question(question)
                .build());
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
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("createAnswer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/question_detail/" + question.getId()));
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 변경 성공 test")
    @Test
    void answerModify() throws Exception {

        mvc.perform(post("/answer/modify/" + answer.getId())
                        .param("content", "change content")
                        .with(csrf()))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("answerModify"))
                .andExpect(redirectedUrl(String.format("/question/detail/%s#answer_%s",
                        answer.getQuestion().getId(), answer.getId())));

        Optional<Answer> optionalAnswer = answerRepository.findById(answer.getId());
        assertThat(optionalAnswer).isNotEmpty();
        assertThat(optionalAnswer.get().getContent()).isEqualTo("change content");
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 변경 실패 test")
    @Test
    void testAnswerModify() throws Exception {

        mvc.perform(post("/answer/modify/" + answer.getId())
                        .param("no", "no")
                        .with(csrf()))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("answerModify"))
                .andExpect(status().isOk())
                .andExpect(view().name("answer/answer_form"));
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 삭제 성공")
    @Test
    void delete() throws Exception {
        mvc.perform(get("/answer/delete/" + answer.getId()))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(redirectedUrl(String.format("/question/detail/%s#answer_%s",
                        answer.getQuestion().getId(), answer.getId())));

        Optional<Answer> optionalAnswer = answerRepository.findById(answer.getId());

        assertThat(optionalAnswer).isEmpty();
    }

    @WithMockUser(username = "anyone")
    @DisplayName("answer 삭제 실패")
    @Test
    void deleteFail() throws Exception {

        mvc.perform(get("/answer/delete/" + answer.getId()))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @DisplayName("answer 추천 성공")
    @Test
    void vote() throws Exception {

        mvc.perform(get("/answer/vote/" + answer.getId()))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("vote"))
                .andExpect(redirectedUrl("/question/detail/" + question.getId()));

        Answer findAnswer = answerRepository.findById(answer.getId()).get();
        assertThat(findAnswer.getVoter()).size().isEqualTo(1);
    }
}