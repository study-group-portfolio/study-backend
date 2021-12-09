package kr.co.studit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class PositionDto {
    String position;
    Integer count;
    ArrayList<String> skills = new ArrayList<>();

}
