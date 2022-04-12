package kr.co.studit.dto.study;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@Setter
public class StudyForm {
    private Long id;
    @ApiModelProperty(example = "스터디 타입")
    private StudyType type;

    @ApiModelProperty(example = "제목")
    private String title;

    @ApiModelProperty(example = "프로필 공개여부")
    @JsonIgnoreProperties
    private boolean profileShare;

    @ApiModelProperty(example = "내용")
    private String content;

    @ApiModelProperty(example = "진행 방식")
    private OnOffStatus status;

    @ApiModelProperty(example = "서울")
    private String region;

    // 예상 진행 기간
    @ApiModelProperty(example = "3달")
    private String duration;

    // 예상 요일
    @ApiModelProperty(example = "주말")
    private String studyDay;

    @ApiModelProperty(example = "[\n" +
            "    {\n" +
            "      \"position\": \"백엔드 개발자\",\n" +
            "      \"totalCount\": 5,\n" +
            "      \"count\": 0,\n" +
            "      \"skills\": [\n" +
            "        \"Spring\",\n" +
            "        \"Java\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"position\": \"프론트엔드 개발자\",\n" +
            "      \"totalCount\": 4,\n" +
            "      \"count\": 0,\n" +
            "      \"skills\": [\n" +
            "        \"React\",\n" +
            "        \"Vue\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]")
    private ArrayList<PositionDto> positions = new ArrayList<>();

    // 모집 기간
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate receptionStart;

    // 모집 종료 기간
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate receptionEnd;
    // 사용 협업 툴
    @ApiModelProperty(example = "[\"Git\"]")
    private ArrayList<String> tools = new ArrayList<>();
}
