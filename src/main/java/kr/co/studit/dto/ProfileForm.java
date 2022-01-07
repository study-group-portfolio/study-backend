package kr.co.studit.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProfileForm {

    private String nickname;

    private String bio;

    // 참여하고 싶은 스터디 유형
    private StudyType studyType;

    // 선호하는 진행 방식
    private OnOffStatus onOffStatus;

    // 지역
    private List<String> regions = new ArrayList<>();

    // 지역 수정 여부
    private Boolean updateRegion;

    // 업무 포지션
    private List<String> positions = new ArrayList<>();

    // 업무 포지션 수정 여부
    private Boolean updatePosition;

    //스킬
    private List<String> skills = new ArrayList<>();

    // 스킬 수정 여부
    private Boolean updateSkill;

    @QueryProjection
    public ProfileForm(String nickname, String bio, StudyType studyType, OnOffStatus onOffStatus, List<String> regions, List<String> positions, List<String> skills) {
        this.nickname = nickname;
        this.bio = bio;
        this.studyType = studyType;
        this.onOffStatus = onOffStatus;
        this.regions = regions;
        this.positions = positions;
        this.skills = skills;
    }

    public Boolean isUpdateRegion() {
        return this.updateRegion;
    }

    public Boolean isUpdatePosition() {
        return this.updatePosition;
    }

    public Boolean isUpdateSkill() {
        return this.updateSkill;
    }
}
