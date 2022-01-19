package kr.co.studit.dto;

import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudyDto extends StudyForm{


    @ApiModelProperty(hidden = true)
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;


}
