package kr.co.studit.study.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionSimpleDto {
    @ApiModelProperty(example = "백엔드 개발자")
    public String positionName;
}
