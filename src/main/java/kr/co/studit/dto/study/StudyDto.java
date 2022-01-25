package kr.co.studit.dto.study;

import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.study.Study;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudyDto extends StudyForm{


    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private LocalDateTime createDate;
    @ApiModelProperty(hidden = true)
    private LocalDateTime modifiedDate;

    @ApiModelProperty(hidden = true)
    // bookmark에 추가할 data
    private Long bookmarkId;
    @ApiModelProperty(hidden = true)
    private Boolean bookmarkState;

}
