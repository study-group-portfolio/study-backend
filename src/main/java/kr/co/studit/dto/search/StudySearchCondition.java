package kr.co.studit.dto.search;

import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudySearchCondition {
    @ApiModelProperty(example = "PROJECT")
    private StudyType type;

    @ApiModelProperty(example = "ON")
    private OnOffStatus status;

    @ApiModelProperty(example = "서울")
    private String region;

    @ApiModelProperty(example = "[\"백엔드 개발자\",\"프론트엔드 개발자\"]")
    private List<String> positions = new ArrayList<>();

    @ApiModelProperty(example = "[\"React\"]")
    private List<String> skills = new ArrayList<>();
}
