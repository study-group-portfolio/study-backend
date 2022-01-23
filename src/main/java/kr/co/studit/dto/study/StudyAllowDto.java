package kr.co.studit.dto.study;


import kr.co.studit.dto.enums.InviteType;
import lombok.Data;

@Data
public class StudyAllowDto {
    Long id;
    Long studyId;
    InviteType type;
    String email;
    boolean allow;

}