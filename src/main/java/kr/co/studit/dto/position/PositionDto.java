package kr.co.studit.dto.position;

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
    String position;
    Integer count;
    Integer totalCount;
    ArrayList<String> skills = new ArrayList<>();

}
