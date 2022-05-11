package kr.co.studit.controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.co.studit.response.ResponseDto;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.activitirigion.domain.OnOffStatus;
import kr.co.studit.study.domain.StudyType;
import kr.co.studit.member.domain.Member;
import kr.co.studit.member.dto.*;
import kr.co.studit.member.domain.MemberDataRepository;
import kr.co.studit.member.service.MemberService;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
public class MemberDocTest {

    static final String BASE_URI = "/api/member";
    static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private String accessToken;





    @Autowired
    private MemberDataRepository memberDataRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    private Member testMember;

    @BeforeEach
    public void beforeTest() throws Exception {

        this.testMember = memberDataRepository.findMemberById(5L);
        setAccessToken();
    }

    public void setAccessToken() throws Exception {
        String uri = BASE_URI + "/signin";
        String email = testMember.getEmail();
        String password = "12345678";
        SigninDto signinDto =
                SigninDto.builder()
                        .email(email)
                        .password(password)
                        .build();

        String jsonObject = new Gson().toJson(signinDto);
        //when
        ResultActions result = postPerform(uri, jsonObject);
        //then
        ResultActions andResult = result.andExpect(status().isOk());

        ResponseDto res = new ResponseDto();
        res =  new Gson().fromJson(andResult.andReturn().getResponse().getContentAsString(), ResponseDto.class);
        LinkedTreeMap<String, String> data = (LinkedTreeMap<String, String>) res.getData();
        this.accessToken = data.get("accesToken");
    }

    private String getAccessToken() {
        return this.accessToken;
    }

////    @BeforeEach
//    public void setUp(WebApplicationContext webApplicationContext,
//                      RestDocumentationContextProvider restDocumentation) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(documentationConfiguration(restDocumentation))
//                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
//                .build();
//    }


    private ResultActions putPerformWithAuthenticated(String uri, String json) throws Exception {
        return this.mockMvc.perform(
                put(uri).header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken() )
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(APPLICATION_JSON_UTF8)
                        .content(json)
        );
    }

    private ResultActions putPerform(String uri, String json) throws Exception {
        return this.mockMvc.perform(
                put(uri)
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(APPLICATION_JSON_UTF8)
                        .content(json)
        );
    }

    private ResultActions postPerform(String uri, String json) throws Exception {
        return this.mockMvc.perform(
                post(uri)
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(APPLICATION_JSON_UTF8)
                        .content(json)
        );
    }

    private ResultActions getPreform(String uri, String pathParm) throws Exception {
        ResultActions result = this.mockMvc.perform(get(uri, pathParm)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
        );
        return result;
    }

    private ResultActions getPreformWithAuthenticated(String uri, String pathParm) throws Exception {
        ResultActions result = this.mockMvc.perform(get(uri, pathParm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken() )
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
        );
        return result;
    }

    private ResultActions getPreformWithAuthenticated(String uri) throws Exception {
        ResultActions result = this.mockMvc.perform(get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken() )
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
        );
        return result;
    }

    @Test
    public void signUp() throws Exception {
        //given
        String uri = BASE_URI + "/signup";
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
                postPerform(uri, json);
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
    public void checkNickname() throws Exception {
        //given
        String uri = BASE_URI + "/checkNickname/{nickname}";

        String newNickname = "nickname";
        //when
       ResultActions result = getPreform(uri, newNickname);
       //then
        result.andExpect(status().isOk())
                .andDo(document("checkNickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임").attributes(key("type").value("String"))
                        )
                ));

    }

    @Test
    public void checkEmail() throws Exception {
        //given
        String uri = BASE_URI + "/signup/{email}";
        String email = "email@studit.co.kr";

        //when
        ResultActions result = getPreform(uri, email);

        //then
        result.andExpect(status().isOk())
                .andDo(document("checkEmail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("email").description("이메일").attributes(key("type").value("String"))
                        )
                ));
        
    }

    @Test
    public void login() throws Exception {
        //given
        String uri = BASE_URI + "/signin";
        String email = testMember.getEmail();
        String password = "12345678";
        SigninDto signinDto =
                SigninDto.builder()
                    .email(email)
                    .password(password)
                    .build();

        String jsonObject = new Gson().toJson(signinDto);
        //when
        ResultActions result = postPerform(uri, jsonObject);
        //then
        ResultActions andResult = result.andExpect(status().isOk())
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

    @Test
    public void updateProfile() throws Exception {
        //given
        String uri = BASE_URI + "/profile";
        ProfileForm profileForm = new ProfileForm();
        List<String> positionNames = new ArrayList<>();
        positionNames.add("백엔드 개발자");
        List<String> areas = new ArrayList<>();
        areas.add("서울");
        areas.add("대구");
        List<String> skillNames = new ArrayList<>();
        skillNames.add("Spring");
        skillNames.add("Java");
        List<String> portpolios = new ArrayList<>();
        portpolios.add("https://naver.com");
        portpolios.add("https://github.com/study-group-portfolio/study-backend");
        profileForm.setPositions(positionNames);
        profileForm.setPortpolios(portpolios);
        profileForm.setSkills(skillNames);
        profileForm.setRegions(areas);
        profileForm.setOnOffStatus(OnOffStatus.ON);
        profileForm.setNickname(testMember.getNickname());
        profileForm.setBio("자기소개 하는 곳 ~~");
        profileForm.setEmail(testMember.getEmail());
        profileForm.setPublicProfile(true);
        profileForm.setStudyType(StudyType.SHARE);

        String jsonObj = new Gson().toJson(profileForm);

        //when
        ResultActions result = putPerformWithAuthenticated(uri, jsonObj);

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("publicProfile").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개").optional(),
                                fieldWithPath("studyType").type(JsonFieldType.STRING).description("스터디 유형").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("onOffStatus").type(JsonFieldType.STRING).description("진행방식").attributes(key("remark").value("ON/ONOFF/OFF")),
                                subsectionWithPath("regions.[]").type(JsonFieldType.ARRAY).description("활동지역").optional(),
                                subsectionWithPath("positions.[]").type(JsonFieldType.ARRAY).description("포지션").optional(),
                                subsectionWithPath("skills.[]").type(JsonFieldType.ARRAY).description("스킬").optional(),
                                subsectionWithPath("portpolios.[]").type(JsonFieldType.ARRAY).description("포트폴리오").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data.img").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.bio").type(JsonFieldType.STRING).description("자기소개"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.studyType").type(JsonFieldType.STRING).description("스터디 유형"),
                                fieldWithPath("data.onOffStatus").type(JsonFieldType.STRING).description("진행 방식"),
                                fieldWithPath("data.publicProfile").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("data.regions.[]").type(JsonFieldType.ARRAY).description("활동지역"),
                                fieldWithPath("data.positions.[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.portpolios.[]").type(JsonFieldType.ARRAY).description("포트폴리오")
                        )
                ));

    }

    @Test
    public void updateBasicProfile() throws Exception {
        //given
        String uri = BASE_URI + "/profile/basic";
        BasicProfileForm basicProfileForm = new BasicProfileForm();
        String email = testMember.getEmail();
        String nickname = "newNickname";
        basicProfileForm.setEmail(email);
        basicProfileForm.setNickname(nickname);
        String jsonObj = new Gson().toJson(basicProfileForm);

        //when
        ResultActions result = putPerformWithAuthenticated(uri, jsonObj);

        //then
        result.andExpect(status().isOk())
                .andDo(document("update-profile-basic",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("img").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")

                        )
                        ,responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data.img").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                        )
                    )
                );

    }
    
    @Test
    public void getMyprofile() throws Exception {
        //given
        String uri = BASE_URI + "/profile/myProfile";

        
        //when
        ResultActions result = getPreformWithAuthenticated(uri);

        //then
        result.andExpect(status().isOk())
                .andDo(document("myPage",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").optional(),
                                fieldWithPath("data.img").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.bio").type(JsonFieldType.STRING).description("자기소개"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.studyType").type(JsonFieldType.STRING).description("스터디 유형"),
                                fieldWithPath("data.onOffStatus").type(JsonFieldType.STRING).description("진행 방식"),
                                fieldWithPath("data.publicProfile").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("data.regions.[]").type(JsonFieldType.ARRAY).description("활동지역"),
                                fieldWithPath("data.positions.[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.portpolios.[]").type(JsonFieldType.ARRAY).description("포트폴리오")
                        ))

                );
        
    }

    @Test
    public void getProfile() throws Exception {
        //given
        String uri = BASE_URI + "/profile/{id}";
        Long id = testMember.getId();
        //when
        ResultActions result = getPreform(uri, id.toString());

        //then
        result.andExpect(status().isOk())
                .andDo(document("getProfile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id").attributes(key("type").value("String"))
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").optional(),
                                fieldWithPath("data.img").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.bio").type(JsonFieldType.STRING).description("자기소개"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.studyType").type(JsonFieldType.STRING).description("스터디 유형"),
                                fieldWithPath("data.onOffStatus").type(JsonFieldType.STRING).description("진행 방식"),
                                fieldWithPath("data.publicProfile").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("data.regions.[]").type(JsonFieldType.ARRAY).description("활동지역"),
                                fieldWithPath("data.positions.[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.portpolios.[]").type(JsonFieldType.ARRAY).description("포트폴리오")
                        )
                        ));

    }
    @Test
    public void updatePassword() throws Exception {
        //given
        String uri = BASE_URI + "/profile/password";
        UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();
        updatePasswordForm.setCurrentPassword("12345678");
        updatePasswordForm.setNewPassword("1234567");
        updatePasswordForm.setNewConfirmPassword("1234567");
        String jsonObj = new Gson().toJson(updatePasswordForm);

        //when
        ResultActions result = putPerformWithAuthenticated(uri, jsonObj);

        //then
        result.andExpect(status().isOk())
                .andDo(document("update-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("currentPassword").type(JsonFieldType.STRING).description("현재 비밀번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호"),
                                fieldWithPath("newConfirmPassword").type(JsonFieldType.STRING).description("새로운 비밀번호 확인")

                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")

                        ))

                );

    }
    @Test
    public void checkEmailToken() throws Exception {
        //given
        String uri = BASE_URI + "/checkEmailToken/{token}/{email}";

        testMember.generateEmailCheckToken();


        //when
        ResultActions result = this.mockMvc.perform(get(uri, testMember.getEmaiCheckToken(), testMember.getEmail()));

        //then
        result.andExpect(status().isOk()).andDo(document("email-verife",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("token").description("토큰").attributes(key("type").value("String")),
                        parameterWithName("email").description("이메일").attributes(key("type").value("String"))
                ),
                responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.accesToken").type(JsonFieldType.STRING).description("acess_token"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("refresh_token")
                ))
        );

        assertThat(testMember.isEmailVerified()).isTrue();

    }
    @Test
    public void sendEmailForResetPassword() throws Exception {
        //given
        String uri = BASE_URI + "/password/send-mail";
        FindPasswordDto findPasswordDto = new FindPasswordDto();
        findPasswordDto.setEmail(testMember.getEmail());
        LocalDateTime modifiedDate = testMember.getModifiedDate();
        String jsonObj = new Gson().toJson(findPasswordDto);
        LocalDateTime now = LocalDateTime.now();
        //when
        ResultActions result = postPerform(uri, jsonObj);

        //then
        result.andExpect(status().isOk())
                .andDo(document("send-email-reset-password",  preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data").ignored()

                        )));
        assertThat(testMember.getPasswordFindTokenGeneratedAt().isAfter(now)).isTrue();

    }

    @Test
    public void checkFindPasswordToken() throws Exception {
        //given
        String uri = BASE_URI + "/checkFindPasswordToken/{token}/{email}";
        testMember.generatePasswordFindToken();
        String token = testMember.getPasswordFindToken();


        //when
        ResultActions result = this.mockMvc.perform(get(uri, token, testMember.getEmail())
                                        .contentType(APPLICATION_JSON_UTF8)
                                        .accept(APPLICATION_JSON_UTF8));

        //then
        result.andExpect(status().isOk()).andDo(document("email-verife-reset",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("token").description("토큰").attributes(key("type").value("String")),
                        parameterWithName("email").description("이메일").attributes(key("type").value("String"))
                ),
                responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.resetToken").type(JsonFieldType.STRING).description("리셋 토큰")
                ))
        );


    }

    @Test
    public void resetPassword() throws Exception {
        //given
        String uri = BASE_URI + "/password";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        testMember.generatePasswordFindToken();
        String resetToken = testMember.getPasswordFindToken();
        resetPasswordDto.setEmail(testMember.getEmail());
        resetPasswordDto.setPassword("1234567");
        resetPasswordDto.setConfirmPassword("1234567");
        resetPasswordDto.setResetToken(resetToken);
        String jsoObj = new Gson().toJson(resetPasswordDto);

        //when
        ResultActions result = putPerform(uri, jsoObj);

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("rest-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("confirmPassword").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("resetToken").type(JsonFieldType.STRING).description("리셋 토큰")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지")

                        )


                ));

    }

    @Test
    public void getDefaultMemberList() throws Exception {
        //given
        String uri = BASE_URI + "/search?page=0";


        //when
        ResultActions result = this.mockMvc.perform(post(uri).accept(APPLICATION_JSON_UTF8));
        //then
        result.andExpect(status().isOk()).
                andDo(document("memberlist-v1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("요청 페이지 (기본값 : 0) ").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data.content[].memberId").type(JsonFieldType.NUMBER).description("멤버아이디"),
                                fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.content[].positionName[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.content[].area[]").type(JsonFieldType.ARRAY).description("활동지역"),
                                fieldWithPath("data.content[].skillName[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.content[].bookmarkId").type(JsonFieldType.NUMBER).description("북마크아이디").ignored(),
                                fieldWithPath("data.content[].bookmarkState").type(JsonFieldType.BOOLEAN).description("북마크상태").ignored(),
                                fieldWithPath("data.pageable.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("data.pageable.size").type(JsonFieldType.NUMBER).description("페이지 사이즈(기본값 : 12)"),
                                fieldWithPath("data.pageable.totalElements").type(JsonFieldType.NUMBER).description("총 요소의 수"),
                                fieldWithPath("data.pageable.totalPages").type(JsonFieldType.NUMBER).description("총 페이지의 수"),
                                fieldWithPath("data.pageable.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소의 수"),
                                fieldWithPath("data.pageable.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("data.pageable.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data.pageable.empty").type(JsonFieldType.BOOLEAN).description("요소의 유무")
                )

                ));
    }

    @Test
    public void getMemberListWithAthenticated() throws Exception {
        //given

        String uri = BASE_URI + "/search?page=0";
        MemberSearchCondition condition = new MemberSearchCondition();
        List<String> positionNames = new ArrayList<>();
        positionNames.add("백엔드 개발자");
        positionNames.add("프론트엔드 개발자");
        List<String> areas = new ArrayList<>();
        areas.add("서울");
        areas.add("충청");
        List<String> skillNames = new ArrayList<>();
        skillNames.add("Spring");
        skillNames.add("Java");
        condition.setRegions(areas);
        condition.setPositions(positionNames);
        condition.setStudyType(StudyType.SHARE);
        condition.setSkills(skillNames);
        condition.setOnOffStatus(OnOffStatus.ON);

        String json = new Gson().toJson(condition);

        //when
        ResultActions result = this.mockMvc.perform(post(uri).
                header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andDo(document("memberlist-v2",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("studyType").type(JsonFieldType.STRING).description("비밀번호 확인").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("onOffStatus").type(JsonFieldType.STRING).description("선호하는 활동 유형").attributes(key("remark").value("ON/ONOFF/OFF")),
                                subsectionWithPath("regions.[]").type(JsonFieldType.ARRAY).description("활동지역").optional(),
                                subsectionWithPath("positions.[]").type(JsonFieldType.ARRAY).description("포지션").optional(),
                                subsectionWithPath("skills.[]").type(JsonFieldType.ARRAY).description("스킬").optional()
                        ),
                        requestParameters(
                                parameterWithName("page").description("요청 페이지 (기본값 : 0) ").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data.content[].memberId").type(JsonFieldType.NUMBER).description("멤버아이디"),
                                fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.content[].positionName[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.content[].area[]").type(JsonFieldType.ARRAY).description("활동지역"),
                                fieldWithPath("data.content[].skillName[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.content[].bookmarkId").type(JsonFieldType.NUMBER).description("북마크아이디").ignored(),
                                fieldWithPath("data.content[].bookmarkState").type(JsonFieldType.BOOLEAN).description("북마크상태").ignored(),
                                fieldWithPath("data.pageable.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("data.pageable.size").type(JsonFieldType.NUMBER).description("페이지 사이즈(기본값 : 12)"),
                                fieldWithPath("data.pageable.totalElements").type(JsonFieldType.NUMBER).description("총 요소의 수"),
                                fieldWithPath("data.pageable.totalPages").type(JsonFieldType.NUMBER).description("총 페이지의 수"),
                                fieldWithPath("data.pageable.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소의 수"),
                                fieldWithPath("data.pageable.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("data.pageable.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data.pageable.empty").type(JsonFieldType.BOOLEAN).description("요소의 유무")
                        )

                ));

    }

    @Test
    public void withdrawal() throws Exception {
        //given
        String uri = BASE_URI + "/withdrawal";

        //when
        ResultActions result = this.mockMvc.perform(delete(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo((document("withdrawal",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("결과메시지").ignored()

                        )

                        )));

    }
}
