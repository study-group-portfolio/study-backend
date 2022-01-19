package kr.co.studit.dto;

import lombok.Data;

@Data
public class InvitationDto {
    Long studyId;
    String position;
    String message;
    String inviteMember;
}
