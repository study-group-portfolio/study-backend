package kr.co.studit.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyPositionDto {
    Long studyId;
    List<PositionDto> positions;

}
