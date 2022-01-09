package kr.co.studit.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmDto {

    Long id;
    String email;
    String title;
    String position;
    String message;

}
