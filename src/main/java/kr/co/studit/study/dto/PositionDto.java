package kr.co.studit.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto {
    String positionName;
    Integer count = 0;
    Integer totalCount = 0;
    ArrayList<String> skills = new ArrayList<>();
    boolean recruited;

}
