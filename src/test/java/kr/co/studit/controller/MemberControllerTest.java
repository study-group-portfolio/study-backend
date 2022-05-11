package kr.co.studit.controller;

import com.google.gson.Gson;
import kr.co.studit.member.dto.SigninDto;
import kr.co.studit.member.dto.SignupDto;
import kr.co.studit.member.domain.MemberDataRepository;
import kr.co.studit.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    static final String BASE_URL = "/api/member";
    static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    @Autowired
    MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberDataRepository memberDataRepository;

    @BeforeEach
    void beforeEach() {
        SignupDto signupDto = SignupDto.builder()
                .email("studit@studit.com")
                .password("1234567")
                .confirmPassword("1234567")
                .build();
        memberService.createMember(signupDto);

    }


    @DisplayName("회원가입 테스트 - 정상 입력값")
    @Test
    public void sucsesSignup() throws Exception {
        String url = BASE_URL + "/signup";
        SignupDto signupDto = SignupDto.builder()
                .email("jinu@gmail.com")
                .password("1234567")
                .confirmPassword("1234567")
                .nickname("닉네임")
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(signupDto);
        mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원가입 테스트 - 잘못된 입력값")
    @Test
    public void failedSignup() throws Exception {
        String url = BASE_URL + "/signup";
        SignupDto signupDto = SignupDto.builder()
                .email("jinu@gmail.com")
                .password("1234567")
                .confirmPassword("123456")
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(signupDto);
        mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인 테스트 - 정상 입력값")
    @Test
    public void sucsessLogin() throws Exception {
        //given
        String url = BASE_URL + "/signin";
        SigninDto signinDto = SigninDto.builder()
                .email("studit@studit.com")
                .password("1234567")
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(signinDto);
        mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());

    }

}