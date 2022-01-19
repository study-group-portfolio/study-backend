package kr.co.studit.dto.study;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudyDto extends StudyForm{


    @ApiModelProperty(hidden = true)
    private Long id;

    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    // bookmark에 추가할 data
    private Long bookmarkId;

    private Boolean bookmarkState;

}
