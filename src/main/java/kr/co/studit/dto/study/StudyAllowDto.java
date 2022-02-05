package kr.co.studit.dto.study;


import io.swagger.annotations.ApiModelProperty;
import kr.co.studit.dto.enums.InviteType;
import lombok.Data;

@Data
public class StudyAllowDto {
    @ApiModelProperty(example = "115")
    Long id;
    @ApiModelProperty(example = "107")
    Long studyId;
    @ApiModelProperty(example = "STUDY")
    InviteType type;
    @ApiModelProperty(example = "test2@test.com")
    String email;
    @ApiModelProperty(example = "true")
    boolean allow;

}
