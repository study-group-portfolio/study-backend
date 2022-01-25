package kr.co.studit.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InvitationDto {
    @ApiModelProperty(example = "107")
    Long studyId;
    @ApiModelProperty(example = "백엔드")
    String position;
    @ApiModelProperty(example = "저희 팀원 하실래요?")
    String message;
    @ApiModelProperty(example = "test2@test.com")
    String inviteMember;
}
