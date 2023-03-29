package springboot.jump.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import springboot.jump.aggregate.user.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("권한 없을시 login page로 redirect")
    void notFound() throws Exception {
        mvc.perform(get("/user/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/user/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("권한이 있다면 profile페이지 접근 가능")
    void successFound() throws Exception {
        userService.create("user","email123@naver.com","1111");

        mvc.perform(get("/user/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"));
    }

    @Test
    @DisplayName("가입 get 요청")
    void singup() throws Exception {
        mvc.perform(get("/user/signup"))
                .andExpect(view().name("sign_form"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("가입 login 요청")
    void login() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(view().name("login_form"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("가입 요청")
    void signupPass() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "username");
        map.add("password", "11111111");
        map.add("checkPassword", "11111111");
        map.add("email", "email@naver.com");

        mvc.perform(post("/user/signup")
                        .params(map).with(csrf()))
                .andExpect(view().name("redirect:/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("가입 요청 실패")
    void signupFail() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "username");
        map.add("password", "1111");
        map.add("checkPassword", "1234");
        map.add("email", "email@naver.com");

        mvc.perform(post("/user/signup")
                        .params(map).with(csrf()))
                .andExpect(view().name("sign_form"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 찾기 get 요청")
    void findPassword() throws Exception {
        mvc.perform(get("/user/findPassword"))
                .andExpect(view().name("user/find_password"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 찾기 post 요청")
    void testFindPassword() throws Exception {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", "naver@naver.com");
        mvc.perform(get("/user/findPassword")
                        .params(map))
                .andExpect(view().name("user/find_password"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 찾기 post 요청 실패")
    void testFailFindPassword() throws Exception {
        mvc.perform(get("/user/findPassword"))
                .andExpect(view().name("user/find_password"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("uuidcheck get요청")
    void uuidCheck() throws Exception {
        mvc.perform(get("/user/uuidcheck"))
                .andExpect(view().name("user/uuid_check"))
                .andExpect(status().isOk());
    }

    @Test
    void changePwd() {
    }

    @Test
    void testChangePwd() {
    }

    @Test
    void changePasswordAuth() {
    }

    @Test
    void testChangePasswordAuth() {
    }

    @Test
    void showProfile() {
    }

    @Test
    void testShowProfile() {
    }
}