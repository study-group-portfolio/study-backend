package kr.co.studit.study.dto;

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
    @ApiModelProperty(example = "백엔드 개발자")
    public String positionTypeName;
    public List<String> positionNameList = new ArrayList<>();

    public PositionTypeDto(String positionTypeName) {
        this.positionTypeName = positionTypeName;
    }
}
