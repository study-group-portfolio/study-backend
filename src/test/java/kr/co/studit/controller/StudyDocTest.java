package kr.co.studit.controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.co.studit.dto.member.SigninDto;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.study.StudyForm;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.study.Study;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.repository.study.StudyDataRepository;
import kr.co.studit.service.MemberService;
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
import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
public class StudyDocTest {

    static final String BASE_URI = "/api/study";
    static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private String accessToken;

    @Autowired
    private MemberDataRepository memberDataRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    StudyDataRepository studyDataRepository;

    @Autowired
    private MockMvc mockMvc;

    private Member testMember;

    @BeforeEach
    public void beforeTest() throws Exception {

        this.testMember = memberDataRepository.findMemberById(5L);
        setAccessToken();
    }


    public void setAccessToken() throws Exception {
        String uri =  "/api/member/signin";
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
    public void createStudy() throws Exception {
        //given
        String uri = BASE_URI;

        StudyForm studyForm = new StudyForm();
        studyForm.setType(StudyType.PROJECT);
        studyForm.setRegion("서울");
        studyForm.setStudyDay("주말");
        studyForm.setContent("스터디에 대한 내용");
        studyForm.setDuration("3달");
        PositionDto positionDto1 = new PositionDto();
        positionDto1.setPosition("백엔드 개발자");
        ArrayList<String> skills1 = new ArrayList<>();
        skills1.add("Java");
        skills1.add("Spring");
        positionDto1.setSkills(skills1);
        positionDto1.setCount(0);
        positionDto1.setTotalCount(3);
        PositionDto positionDto2 = new PositionDto();
        positionDto2.setPosition("프론트엔드 개발자");

        positionDto2.setCount(0);
        positionDto2.setTotalCount(3);
        ArrayList<String> skills2 = new ArrayList<>();
        positionDto2.setSkills(skills2);
        skills2.add("React");
        skills2.add("Vue");

        ArrayList<PositionDto> positionDtos = new ArrayList<>();
        positionDtos.add(positionDto1);
        positionDtos.add(positionDto2);
        studyForm.setPositions(positionDtos);
        studyForm.setTitle("취업용 포트폴리오 같이 하실분 구합니다.");
        studyForm.setStatus(OnOffStatus.ON);
        System.out.println("LocalDate.now() = " + LocalDate.now());
        System.out.println("LocalDate.now() = " + LocalDate.now().plusDays(7));
        LocalDate startDate = LocalDate.of(2022, 04, 12);
        LocalDate endDate = startDate.plusDays(7);

        studyForm.setReceptionStart(startDate.toString());
        studyForm.setReceptionEnd(endDate.toString());
        ArrayList<String> tools = new ArrayList<>();
        tools.add("Git");
        studyForm.setTools(tools);
        studyForm.setProfileShare(true);
        String json = new Gson().toJson(studyForm);
        System.out.println(json);
        //when
        ResultActions result = this.mockMvc.perform(post(uri).header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(json));

        //then

        result.andExpect(status().is2xxSuccessful())
                .andDo(document("create-study",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING).description("스터디 유형").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("profileShare").type(JsonFieldType.BOOLEAN).description("내 프로핑 공개여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("활동 지역"),
                                fieldWithPath("duration").type(JsonFieldType.STRING).description("활동 기간"),
                                fieldWithPath("studyDay").type(JsonFieldType.STRING).description("스터디 날짜"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("활동 유형").attributes(key("remark").value("ON/ONOFF/OFF")),
                                fieldWithPath("receptionStart").type(JsonFieldType.STRING).description("모집 시작 날짜"),
                                fieldWithPath("receptionEnd").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                subsectionWithPath("tools.[]").type(JsonFieldType.ARRAY).description("협업 툴").optional(),
                                subsectionWithPath("positions.[].position").type(JsonFieldType.STRING).description("포지션"),
                                subsectionWithPath("positions.[].count").type(JsonFieldType.NUMBER).description("모집된 인원"),
                                subsectionWithPath("positions.[].totalCount").type(JsonFieldType.NUMBER).description("포지션 총 모집 인원"),
                                subsectionWithPath("positions.[].skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                subsectionWithPath("positions.[].recruited").type(JsonFieldType.BOOLEAN).description("모집상태").ignored()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("스터디 id"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("스터디 타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.profileShare").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("온오프라인"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("활동 지역"),
                                fieldWithPath("data.duration").type(JsonFieldType.STRING).description("활동 기간"),
                                fieldWithPath("data.studyDay").type(JsonFieldType.STRING).description("스터디 날짜"),
                                fieldWithPath("data.receptionStart").type(JsonFieldType.STRING).description("모집 시작 날짜"),
                                fieldWithPath("data.receptionEnd").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("작성 날짜"),
                                fieldWithPath("data.modifiedDate").type(JsonFieldType.STRING).description("수정 날짜"),
                                fieldWithPath("data.tools.[]").type(JsonFieldType.ARRAY).description("협업 툴"),
                                fieldWithPath("data.positions.[].position").type(JsonFieldType.STRING).description("포지션"),
                                fieldWithPath("data.positions.[].count").type(JsonFieldType.NUMBER).description("모집된 인원"),
                                fieldWithPath("data.positions.[].totalCount").type(JsonFieldType.NUMBER).description("포지션 총 모집 인원"),
                                fieldWithPath("data.positions.[].recruited").type(JsonFieldType.BOOLEAN).description("false : 모집중"),
                                fieldWithPath("data.positions.[].skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.bookmarkId").type(JsonFieldType.NUMBER).description("북마크아이디").ignored(),
                                fieldWithPath("data.bookmarkState").type(JsonFieldType.BOOLEAN).description("북마크상태").ignored()
                        )

                ));
    }
    
    @Test
    public void getStudy() throws Exception {
        //given
        String uri = BASE_URI + "/{id}";
        Study study = testMember.getStudys().get(0);
        Long id = study.getId();
        //when
        ResultActions result = getPreform(uri, id.toString());

        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(document("get-study",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id").attributes(key("type").value("String"))
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("스터디 id"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("스터디 타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.profileShare").type(JsonFieldType.BOOLEAN).description("프로필 공개여부"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("온오프라인"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("활동 지역"),
                                fieldWithPath("data.duration").type(JsonFieldType.STRING).description("활동 기간"),
                                fieldWithPath("data.studyDay").type(JsonFieldType.STRING).description("스터디 날짜"),
                                fieldWithPath("data.receptionStart").type(JsonFieldType.STRING).description("모집 시작 날짜"),
                                fieldWithPath("data.receptionEnd").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("작성 날짜"),
                                fieldWithPath("data.modifiedDate").type(JsonFieldType.STRING).description("수정 날짜"),
                                fieldWithPath("data.tools.[]").type(JsonFieldType.ARRAY).description("협업 툴"),
                                fieldWithPath("data.positions.[]").type(JsonFieldType.ARRAY).description("포지션"),
                                fieldWithPath("data.positions.[].position").type(JsonFieldType.STRING).description("포지션"),
                                fieldWithPath("data.positions.[].count").type(JsonFieldType.NUMBER).description("모집된 인원"),
                                fieldWithPath("data.positions.[].totalCount").type(JsonFieldType.NUMBER).description("포지션 총 모집 인원"),
                                fieldWithPath("data.positions.[].skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                fieldWithPath("data.positions.[].recruited").type(JsonFieldType.BOOLEAN).description("false: 모집중"),
                                fieldWithPath("data.bookmarkId").type(JsonFieldType.NUMBER).description("북마크아이디").ignored(),
                                fieldWithPath("data.bookmarkState").type(JsonFieldType.BOOLEAN).description("북마크상태").ignored()
                        )

                ));
        
    }

    @Test
    public void updateStudy() throws Exception {
        //given
        String uri = BASE_URI+"/{id}";
        Long id = testMember.getStudys().get(0).getId();


        StudyForm studyForm = new StudyForm();
        studyForm.setType(StudyType.PROJECT);
        studyForm.setRegion("서울");
        studyForm.setStudyDay("주말");
        studyForm.setContent("스터디에 대한 내용");
        studyForm.setDuration("3달");
        PositionDto positionDto1 = new PositionDto();
        positionDto1.setPosition("백엔드 개발자");
        ArrayList<String> skills1 = new ArrayList<>();
        skills1.add("Java");
        skills1.add("Spring");
        positionDto1.setSkills(skills1);
        positionDto1.setCount(0);
        positionDto1.setTotalCount(3);
        PositionDto positionDto2 = new PositionDto();
        positionDto2.setPosition("프론트엔드 개발자");
        //TODO 포지션 카운트 업데이트는 유효성 체크가 필요할듯 현재 스터디 포지션 인원이 고려 되야함
        positionDto2.setCount(0);
        positionDto2.setTotalCount(3);
        ArrayList<String> skills2 = new ArrayList<>();
        positionDto2.setSkills(skills2);
        skills2.add("React");
        skills2.add("Vue");

        ArrayList<PositionDto> positionDtos = new ArrayList<>();
        positionDtos.add(positionDto1);
        positionDtos.add(positionDto2);
        studyForm.setPositions(positionDtos);
        studyForm.setTitle("취업용 포트폴리오 같이 하실분 구합니다.");
        studyForm.setStatus(OnOffStatus.ON);
        studyForm.setReceptionStart(LocalDate.now().toString());
        studyForm.setReceptionEnd(LocalDate.now().plusDays(7).toString());
        ArrayList<String> tools = new ArrayList<>();
        tools.add("Git");
        studyForm.setTools(tools);
        studyForm.setProfileShare(true);
        String json = new Gson().toJson(studyForm);
        //when
        ResultActions result = this.mockMvc.perform(put(uri, id).header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(json));

        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(document("update-study",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                        parameterWithName("id").description("id").attributes(key("type").value("String"))
                        ),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING).description("스터디 유형").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("profileShare").type(JsonFieldType.BOOLEAN).description("내 프로핑 공개여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("활동 지역"),
                                fieldWithPath("duration").type(JsonFieldType.STRING).description("활동 기간"),
                                fieldWithPath("studyDay").type(JsonFieldType.STRING).description("스터디 날짜"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("활동 유형").attributes(key("remark").value("ON/ONOFF/OFF")),
                                fieldWithPath("receptionStart").type(JsonFieldType.STRING).description("모집 시작 날짜"),
                                fieldWithPath("receptionEnd").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                subsectionWithPath("tools.[]").type(JsonFieldType.ARRAY).description("협업 툴").optional(),
                                subsectionWithPath("positions.[].position").type(JsonFieldType.STRING).description("포지션"),
                                subsectionWithPath("positions.[].count").type(JsonFieldType.NUMBER).description("모집된 인원"),
                                subsectionWithPath("positions.[].totalCount").type(JsonFieldType.NUMBER).description("포지션 총 모집 인원"),
                                subsectionWithPath("positions.[].skills.[]").type(JsonFieldType.ARRAY).description("스킬"),
                                subsectionWithPath("positions.[].recruited").type(JsonFieldType.BOOLEAN).description("모집상태").ignored()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("data").ignored()
                        )
                ));

    }

    @Test
    public void deleteStudy() throws Exception {
        //given
        String uri = BASE_URI+"/{id}";
        Long id = testMember.getStudys().get(0).getId();

        //when
        ResultActions result = this.mockMvc.perform(delete(uri, id.toString()).header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));


        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(document("delete-study",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id").attributes(key("type").value("String"))
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지").ignored(),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("data").ignored()
                        ))
                );

    }

    @Test
    public void getStudyList() throws Exception {
        //given
        String uri = BASE_URI;

        //when
        ResultActions result = this.mockMvc.perform(get(uri+"?page=0")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));

        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print());


    }

}
