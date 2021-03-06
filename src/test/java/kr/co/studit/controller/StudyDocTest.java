package kr.co.studit.controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kr.co.studit.dto.member.SigninDto;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.StudySearchCondition;
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
import java.util.List;

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

//        this.testMember = memberDataRepository.findMemberById(5L);
        this.testMember = memberDataRepository.findMemberByEmail("studit0@studit.co.kr");
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
        studyForm.setRegion("??????");
        studyForm.setStudyDay("??????");
        studyForm.setContent("???????????? ?????? ??????");
        studyForm.setDuration("3???");
        PositionDto positionDto1 = new PositionDto();
        positionDto1.setPositionName("????????? ?????????");
        ArrayList<String> skills1 = new ArrayList<>();
        skills1.add("Java");
        skills1.add("Spring");
        positionDto1.setSkills(skills1);
        positionDto1.setCount(0);
        positionDto1.setTotalCount(3);
        PositionDto positionDto2 = new PositionDto();
        positionDto2.setPositionName("??????????????? ?????????");

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
        studyForm.setTitle("????????? ??????????????? ?????? ????????? ????????????.");
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
                                fieldWithPath("type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("profileShare").type(JsonFieldType.BOOLEAN).description("??? ????????? ????????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("duration").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("studyDay").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????").attributes(key("remark").value("ON/ONOFF/OFF")),
                                fieldWithPath("receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                subsectionWithPath("tools.[]").type(JsonFieldType.ARRAY).description("?????? ???").optional(),
                                subsectionWithPath("positions.[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                subsectionWithPath("positions.[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                subsectionWithPath("positions.[].totalCount").type(JsonFieldType.NUMBER).description("????????? ??? ?????? ??????"),
                                subsectionWithPath("positions.[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                subsectionWithPath("positions.[].recruited").type(JsonFieldType.BOOLEAN).description("????????????").ignored()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.profileShare").type(JsonFieldType.BOOLEAN).description("????????? ????????????"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("???????????????"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                                fieldWithPath("data.duration").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.studyDay").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("data.receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.modifiedDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.tools.[]").type(JsonFieldType.ARRAY).description("?????? ???"),
                                fieldWithPath("data.positions.[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.positions.[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("data.positions.[].totalCount").type(JsonFieldType.NUMBER).description("????????? ??? ?????? ??????"),
                                fieldWithPath("data.positions.[].recruited").type(JsonFieldType.BOOLEAN).description("false : ?????????"),
                                fieldWithPath("data.positions.[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                fieldWithPath("data.bookmarkId").type(JsonFieldType.NUMBER).description("??????????????????").ignored(),
                                fieldWithPath("data.bookmarkState").type(JsonFieldType.BOOLEAN).description("???????????????").ignored()
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.profileShare").type(JsonFieldType.BOOLEAN).description("????????? ????????????"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????").attributes(key("remark").value("ON/OFF/ONOFF")),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                                fieldWithPath("data.duration").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.studyDay").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("data.receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.modifiedDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.tools.[]").type(JsonFieldType.ARRAY).description("?????? ???"),
                                fieldWithPath("data.positions.[]").type(JsonFieldType.ARRAY).description("?????????"),
                                fieldWithPath("data.positions.[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.positions.[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("data.positions.[].totalCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.positions.[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                fieldWithPath("data.positions.[].recruited").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????").attributes(key("remark").value("fasle : ?????????")),
                                fieldWithPath("data.bookmarkId").type(JsonFieldType.NUMBER).description("??????????????????").ignored(),
                                fieldWithPath("data.bookmarkState").type(JsonFieldType.BOOLEAN).description("???????????????").ignored()
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
        studyForm.setRegion("??????");
        studyForm.setStudyDay("??????");
        studyForm.setContent("???????????? ?????? ??????");
        studyForm.setDuration("3???");
        PositionDto positionDto1 = new PositionDto();
        positionDto1.setPositionName("????????? ?????????");
        ArrayList<String> skills1 = new ArrayList<>();
        skills1.add("Java");
        skills1.add("Spring");
        positionDto1.setSkills(skills1);
        positionDto1.setCount(0);
        positionDto1.setTotalCount(3);
        PositionDto positionDto2 = new PositionDto();
        positionDto2.setPositionName("??????????????? ?????????");
        //TODO ????????? ????????? ??????????????? ????????? ????????? ???????????? ?????? ????????? ????????? ????????? ?????? ?????????
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
        studyForm.setTitle("????????? ??????????????? ?????? ????????? ????????????.");
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
                                fieldWithPath("type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("profileShare").type(JsonFieldType.BOOLEAN).description("??? ????????? ????????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("duration").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("studyDay").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????").attributes(key("remark").value("ON/ONOFF/OFF")),
                                fieldWithPath("receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                subsectionWithPath("tools.[]").type(JsonFieldType.ARRAY).description("?????? ???").optional(),
                                subsectionWithPath("positions.[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                subsectionWithPath("positions.[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                subsectionWithPath("positions.[].totalCount").type(JsonFieldType.NUMBER).description("????????? ??? ?????? ??????"),
                                subsectionWithPath("positions.[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                subsectionWithPath("positions.[].recruited").type(JsonFieldType.BOOLEAN).description("????????????").ignored()
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
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
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken() )
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));

        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("get-studyList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                fieldWithPath("data.content[].profileShare").type(JsonFieldType.BOOLEAN).description("????????? ????????? ????????????"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.content[].region").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                                fieldWithPath("data.content[].duration").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.content[].studyDay").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("data.content[].positions[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("data.content[].positions[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("data.content[].positions[].totalCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.content[].positions[].recruited").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????").attributes(key("remark").value("false : ?????? ???")),
                                fieldWithPath("data.content[].positions[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                fieldWithPath("data.content[].tools.[]").type(JsonFieldType.ARRAY).description("???"),
                                fieldWithPath("data.content[].receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(key("remark").value("yyyy-mm-dd")),
                                fieldWithPath("data.content[].receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(key("remark").value("yyyy-mm-dd")),
                                fieldWithPath("data.content[].createDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.content[].modifiedDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.content[].bookmarkId").type(JsonFieldType.NUMBER).description("??????????????????").optional(),
                                fieldWithPath("data.content[].bookmarkState").type(JsonFieldType.BOOLEAN).description("???????????????").optional(),
                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("????????? ???, ????????? ?????? ?????? ??????").ignored(),
                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ???"),
                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????").ignored(),
                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("????????? ??????").ignored(),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("??? ????????? ??????"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("?????? ??? ???"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("?????? ???????????? ????????? ???"),
                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("data.pageable.sort.empty").ignored(),
                                fieldWithPath("data.pageable.sort.sorted").ignored(),
                                fieldWithPath("data.pageable.sort.unsorted").ignored(),
                                fieldWithPath("data.sort.empty").ignored(),
                                fieldWithPath("data.sort.sorted").ignored(),
                                fieldWithPath("data.sort.unsorted").ignored()
                        )
                ));
    }

    @Test
    public void getStudyListWithCondition() throws Exception {
        //given
        String uri = BASE_URI + "/search";
        StudySearchCondition condition = new StudySearchCondition();
        condition.setRegion("??????");
        condition.setType(StudyType.PROJECT);
        condition.setStatus(OnOffStatus.ON);
        List<String> skills = new ArrayList<>();
        skills.add("Vue");
//        skills.add("Java");
//        skills.add("Spring");
        condition.setSkills(skills);
        List<String> position = new ArrayList<>();
        position.add("????????? ?????????");
        condition.setPositions(position);

        String json = new Gson().toJson(condition);
        //when
        ResultActions result = this.mockMvc.perform(post(uri+"?page=0&size=5")
                .content(json)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));

        //then
        result.andExpect(status().is2xxSuccessful())
                .andDo(document("get-studyList-with-condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????").attributes(key("remark").value("ON/OFF/ONOFF")),
                                        fieldWithPath("region").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("positions.[]").type(JsonFieldType.ARRAY).description("?????????"),
                                        fieldWithPath("skills.[]").type(JsonFieldType.ARRAY).description("??????")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("??????"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("???????????????").ignored(),
                                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("????????? id"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("??????"),
                                        fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("????????? ??????").attributes(key("remark").value("SHARE/PROJECT")),
                                        fieldWithPath("data.content[].profileShare").type(JsonFieldType.BOOLEAN).description("????????? ????????? ????????????"),
                                        fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("??????"),
                                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.content[].region").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                                        fieldWithPath("data.content[].duration").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                        fieldWithPath("data.content[].studyDay").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                        fieldWithPath("data.content[].positions[].positionName").type(JsonFieldType.STRING).description("?????????"),
                                        fieldWithPath("data.content[].positions[].count").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                        fieldWithPath("data.content[].positions[].totalCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                        fieldWithPath("data.content[].positions[].recruited").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????").attributes(key("remark").value("false : ?????? ???")),
                                        fieldWithPath("data.content[].positions[].skills.[]").type(JsonFieldType.ARRAY).description("??????"),
                                        fieldWithPath("data.content[].tools.[]").type(JsonFieldType.ARRAY).description("???"),
                                        fieldWithPath("data.content[].receptionStart").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(key("remark").value("yyyy-mm-dd")),
                                        fieldWithPath("data.content[].receptionEnd").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(key("remark").value("yyyy-mm-dd")),
                                        fieldWithPath("data.content[].createDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.content[].modifiedDate").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data.content[].bookmarkId").type(JsonFieldType.NUMBER).description("??????????????????").optional(),
                                        fieldWithPath("data.content[].bookmarkState").type(JsonFieldType.BOOLEAN).description("???????????????").optional(),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("????????? ???, ????????? ?????? ?????? ??????").ignored(),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ???"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????").ignored(),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("????????? ??????").ignored(),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("??? ????????? ??????"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("?????? ??? ???"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("?????? ???????????? ????????? ???"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                        fieldWithPath("data.pageable.sort.empty").ignored(),
                                        fieldWithPath("data.pageable.sort.sorted").ignored(),
                                        fieldWithPath("data.pageable.sort.unsorted").ignored(),
                                        fieldWithPath("data.sort.empty").ignored(),
                                        fieldWithPath("data.sort.sorted").ignored(),
                                        fieldWithPath("data.sort.unsorted").ignored()
                                )


                                        ));


    }

}
