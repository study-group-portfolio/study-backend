package kr.co.studit.dto.position;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionSimpleDto {
    @ApiModelProperty(example = "백엔드")
    public String positionName;
}
