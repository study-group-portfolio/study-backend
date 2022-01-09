package kr.co.studit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionApplyDto {
    Long studyId;
    String email;
    String position;
    String message;
}
