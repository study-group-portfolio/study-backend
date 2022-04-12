package kr.co.studit.entity.study;


import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Tool;
import kr.co.studit.entity.common.BaseTimeEntity;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.member.MemberInvitation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Study extends BaseTimeEntity {

    // 스터디 ID
    @Id
    @GeneratedValue
    @Column(name = "study_id")
    private Long id;

    // 방장 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 스터디 유형
    @Enumerated(EnumType.STRING)
    private StudyType type;

    // 타이틀
    private String title;

    // 소개
    private String content;

    // on,off
    @Enumerated(EnumType.STRING)
    private OnOffStatus onOffStatus;


    //    @OneToMany(mappedBy = "study")
//    private List<StudyZone> zones = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyTool> studytool = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL)
    private List<StudyPosition> studyPosition = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL)
    private List<StudySkill> studySkill = new ArrayList<>();


    // 예상 진행 기간
    private String duration;

    // 예상 요일
    private String studyDay;

    // 모집 기간
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private LocalDate receptionStart;
    // 모집 종료 기간
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private LocalDate receptionEnd;



    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL)
    private List<StudyApplication> studyApplication = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL)
    private List<MemberInvitation> memberInvitation = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL)
    private List<StudyParticipation> studyParticipation = new ArrayList<>();

    // 연관관계 메소드
    public void setMember(Member member) {
        this.member = member;
        member.getStudys().add(this);
    }


    // 생성 메소드
    public static Study createStudy(Member member, Region region) {
        Study study = new Study();
        study.setMember(member);
        study.setRegion(region);
        return study;
    }
    // TODO 북마크 상태 줘야함
    public StudyDto toStudyDto() {
        StudyDto studyDto = new StudyDto();
        studyDto.setId(this.getId());
        studyDto.setType(this.getType());
        studyDto.setTitle(this.getTitle());
        studyDto.setContent(this.getContent());
        studyDto.setStatus(this.getOnOffStatus());
        studyDto.setCreateDate(this.getCreatedDate());
        studyDto.setModifiedDate(this.getModifiedDate());
        if (this.getOnOffStatus() != OnOffStatus.OFF) {
            studyDto.setRegion(this.getRegion().getArea());
        }
        studyDto.setDuration(this.getDuration());
        studyDto.setStudyDay(this.getStudyDay());

        studyDto.setReceptionStart(this.getReceptionStart().toString());
        studyDto.setReceptionEnd(this.getReceptionEnd().toString());
        studyDto.setTools(new ArrayList<>());
        List<StudyTool> studyTools = this.getStudytool();
        for (StudyTool studyTool : studyTools) {
            Tool tool = studyTool.getTool();
            String toolName = tool.getToolName();
            studyDto.getTools().add(toolName);
        }


        return studyDto;
    }

}
