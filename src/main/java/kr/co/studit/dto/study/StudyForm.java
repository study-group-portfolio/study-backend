package kr.co.studit.dto.study;

import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class StudyForm {
    private Long id;
    @ApiModelProperty(example = "PROJECT")
    private StudyType type;

    @ApiModelProperty(example = "타이틀")
    private String title;

    @ApiModelProperty(example = "프로필 공개여부")
    private boolean profileShare;

    @ApiModelProperty(example = "콘텐츠")
    private String content;

    @ApiModelProperty(example = "ON")
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
            "      \"position\": \"백엔드\",\n" +
            "      \"totalCount\": 5,\n" +
            "      \"count\": 0,\n" +
            "      \"skills\": [\n" +
            "        \"스프링\",\n" +
            "        \"장고\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"position\": \"프론트\",\n" +
            "      \"totalCount\": 4,\n" +
            "      \"count\": 0,\n" +
            "      \"skills\": [\n" +
            "        \"리엑트\",\n" +
            "        \"뷰\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]")
    private ArrayList<PositionDto> positions = new ArrayList<>();

    // 모집 기간
    @ApiModelProperty(example = "팀원 모집 기간")
    private String receptionStart;

    // 모집 종료 기간
    @ApiModelProperty(example = "팀원 모집 종료 기간")
    private String receptionEnd;
    // 사용 협업 툴
    @ApiModelProperty(example = "[\"Git\"]")
    private ArrayList<String> tools = new ArrayList<>();
}
