package springboot.jump.question;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.RequestHandledEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionControllerTest {

    private MockMvc mvc;


    @Autowired
    private WebApplicationContext context; // MockMvc 객체 생성을 위한 context
    private MockMvc mockMvc;

//    @Autowired
//    TestController testController;	// 테스트를 진행할 controller
//
//    @Before
//    public void setUp() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(testController).build();     // test를 위한 MockMvc 객체 생성. testController 1개만 주입.
////        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();  // test를 위한 MockMvc 객체 생성. 스프링이 로드한 WebApplicationContext의 인스턴스로 작동.(standaloneSetup() 중 택 1)
//    }

    @BeforeEach
    public  void setMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

    }

    @Test
    @DisplayName("/로 갈시 /question/list로 redirect")
    void goHome() throws Exception {
        mvc.perform(get("/"))
                .andExpect(view().name("redirect:/question/list"))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }
    @Test
    @DisplayName(" /question/list로 갈시 model안에 paging 확인")
    void list() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(view().name("question_list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록 안된 페이지 갈시 404")
    void detail() throws Exception {
        mvc.perform(post("/no/404/page"))
                .andExpect(status().is4xxClientError());

    }

//    @Test
//    @WithAnonymousUser
//    @DisplayName(" WithAnonymousUser ")
//    void withAnonymousUser() throws Exception {
//        mvc.perform(get("/question/detail"))
//                .andDo(print());
//
//    }
//
//
//    @Test
//    @WithMockUser
//    void questionCreate() throws Exception {
//        mvc.perform(get("/user/logout"))
//                .andExpect(status().is3xxRedirection());
//    }

    @Test
    void testQuestionCreate() {
    }

    @Test
    void questionModify() {
    }

    @Test
    void testQuestionModify() {
    }

    @Test
    void questionDelete() {
    }
}