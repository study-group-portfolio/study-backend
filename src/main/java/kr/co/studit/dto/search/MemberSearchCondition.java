package kr.co.studit.dto.search;

import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberSearchCondition {

    private StudyType studyType;

    private OnOffStatus onOffStatus;

    private List<String> regions = new ArrayList<>();

    private List<String> positions = new ArrayList<>();

    private List<String> skills = new ArrayList<>();


}
