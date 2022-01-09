package kr.co.studit.dto;

import kr.co.studit.dto.enums.InviteType;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.entity.BaseTimeEntity;
import kr.co.studit.entity.enums.StudyType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmDto extends BaseTimeEntity {

    InviteType type;
    Long id;
    String email;
    String title;
    String position;
    String message;
}
