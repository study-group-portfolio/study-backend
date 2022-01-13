package kr.co.studit.dto.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchMemberDto {

    private Long memberId;
    private String nickname;
    private List<String> positionName = new ArrayList<>();
    private List<String> area = new ArrayList<>();
    private List<String> skillName = new ArrayList<>();


}
