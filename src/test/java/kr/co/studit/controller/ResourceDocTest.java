package kr.co.studit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
public class ResourceDocTest {

    static final String BASE_URI = "/api/resource";
    static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    MockMvc mockMvc;

    @Test
    public void gerPositionList() throws Exception {
        //given
        String uri = BASE_URI + "/position";
        //when
        ResultActions result = mockMvc.perform(get(uri)
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8));

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-positionList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("data.[].positionTypeName").type(JsonFieldType.STRING).description("포지션 타입 이름"),
                                fieldWithPath("data.[].positionNameList.[]").type(JsonFieldType.ARRAY).description("포지션 이름")

                        )

                ));

    }

    @Test
    public void gerSkillList() throws Exception {
        //given
        String uri = BASE_URI + "/skill/{positionName}";
        String positionName = "백엔드 개발자";
        //when
        ResultActions result = mockMvc.perform(get(uri, positionName)
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
                );

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-skillList",
                        pathParameters(
                                parameterWithName("positionName").description("positionName").attributes(key("type").value("String"))
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("data.[].skillName").type(JsonFieldType.STRING).description("스킬 이름")
                        )

                ));

    }

    @Test
    public void getAllSkillList() throws Exception {
        //given
        String uri = BASE_URI + "/skills";
        //when
        ResultActions result = this.mockMvc.perform(get(uri)
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(print());

    }

}
