package kr.co.studit.dto.position;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionApplyDto {
    @ApiModelProperty(example = "107")
    Long studyId;
    @ApiModelProperty(example = "백엔드")
    String position;
    @ApiModelProperty(example = "백엔드 지원합니다")
    String message;
}
