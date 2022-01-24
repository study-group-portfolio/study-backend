package kr.co.studit.dto.member;

import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProfileForm extends BasicProfileForm {

    @ApiModelProperty(example = "자기소개")
    private String bio;

    // 참여하고 싶은 스터디 유형
    @ApiModelProperty(example = "PROJECT")
    private StudyType studyType;

    // 선호하는 진행 방식
    @ApiModelProperty(example = "ON")
    private OnOffStatus onOffStatus;

    // 지역
    @ApiModelProperty(example = "[\n" +
            "\t\"서울\",\"대전\"\n" +
            "  ]")
    private List<String> regions = new ArrayList<>();

    // 업무 포지션
    @ApiModelProperty(example = "[\n" +
            "\t\"백엔드\",\"프론트\"\n" +
            "  ]")
    private List<String> positions = new ArrayList<>();

    //스킬
    @ApiModelProperty(example = " [\n" +
            "\t\"스프링\"\n" +
            "  ]")
    private List<String> skills = new ArrayList<>();

    // 포트폴리오 주소
    @ApiModelProperty(example = "[\n" +
            "\t\"https://naver.com\",\"https://studit.co.kr\"\n" +
            "  ]")
    private List<String> portpolios = new ArrayList<>();



}
