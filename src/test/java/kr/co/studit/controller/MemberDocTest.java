package kr.co.studit.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.co.studit.dto.member.SigninDto;
import kr.co.studit.dto.member.SignupDto;
import kr.co.studit.entity.member.Member;
import kr.co.studit.repository.member.MemberDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
public class MemberDocTest {

    static final String BASE_URL = "/api/member";
    static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MemberDataRepository memberDataRepository;

    @Autowired
    private MockMvc mockMvc;

    private Member testMember;

    @BeforeEach
    public void beforeTest() {
        this.testMember = memberDataRepository.findMemberById(5L);
    }



//    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    private ResultActions getPostPerform(String url, String json) throws Exception {
        return this.mockMvc.perform(
                post(url)
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(APPLICATION_JSON_UTF8)
                        .content(json)
        );
    }


    @Test
    public void signUp() throws Exception {
        //given
        String url = BASE_URL + "/signup";
        SignupDto signupDto = SignupDto.builder()
                .email("jinu@studit.co.kr")
                .password("1234567")
                .confirmPassword("1234567")
                .nickname("닉네임")
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(signupDto);


        //when



        ResultActions result =
                getPostPerform(url, json);

        //then
        result.andExpect(status().isOk())
                .andDo(document("signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("confirmPassword").type(JsonFieldType.STRING).description("비밀번호 확인").attributes(key("remark").value("AorB"))
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.accesToken").type(JsonFieldType.STRING).description("acess_token"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("refresh_token")
                        )
                ));


    }



    @Test
    public void findMember() throws Exception {
        //given


        //when
        mockMvc.perform(get(BASE_URL + "/profile/{id}", 5)
                        .contentType(APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());


    }

    @Test
    public void login() throws Exception {
        //given
        String url = BASE_URL + "/signin";
        String email = testMember.getEmail();
        String password = "12345678";
        SigninDto signinDto =
                SigninDto.builder()
                    .email(email)
                    .password(password)
                    .build();

        String jsonObject = new Gson().toJson(signinDto);



        //when
        ResultActions result = getPostPerform(url, jsonObject);

        //then
        result.andExpect(status().isOk())
                .andDo(document("signin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.accesToken").type(JsonFieldType.STRING).description("acess_token"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("refresh_token")
                        )
                        ));

    }

}
