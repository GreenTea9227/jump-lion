package springboot.jump.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
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
                        .params(map))
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
                        .params(map))
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
    void uuidCheck() {
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