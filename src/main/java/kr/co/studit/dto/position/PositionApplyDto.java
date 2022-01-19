package kr.co.studit.dto.position;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionApplyDto {
    Long studyId;
    String position;
    String message;
}
