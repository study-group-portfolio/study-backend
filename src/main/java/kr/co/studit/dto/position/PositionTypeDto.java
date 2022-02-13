package kr.co.studit.dto.position;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionTypeDto {
    @ApiModelProperty(example = "백엔드")
    public String positionTypeName;
    public List<String> positionNameList = new ArrayList<>();

    public PositionTypeDto(String positionTypeName) {
        this.positionTypeName = positionTypeName;
    }
}
