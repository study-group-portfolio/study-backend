package kr.co.studit.dto;

import kr.co.studit.dto.enums.InviteType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmDto {

    InviteType type;
    Long id;
    Long studyId;
    String email;
    String title;
    String position;
    String message;

    LocalDateTime createDate;
    LocalDateTime modifiedDate;
}
