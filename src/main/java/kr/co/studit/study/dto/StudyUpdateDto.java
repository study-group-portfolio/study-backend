package kr.co.studit.study.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyUpdateDto extends StudyForm{

    @ApiModelProperty(hidden = true)
    private Long id;



}
