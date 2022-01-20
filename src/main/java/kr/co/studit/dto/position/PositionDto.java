package kr.co.studit.dto.position;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class PositionDto {
    String position;
    Integer count;
    Integer totalCount;
    ArrayList<String> skills = new ArrayList<>();

}
