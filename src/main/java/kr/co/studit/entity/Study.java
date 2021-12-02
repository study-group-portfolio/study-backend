package kr.co.studit.entity;


import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Study {

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


    @OneToMany(mappedBy = "study")
    private List<StudyZone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<StudyTool> tools = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<StudySkill> skills = new ArrayList<>();

    // 예상 진행 기간
    private String duration;

    // 예상 요일
    private String durationDay;

    // 모집 기간
    private String receptionStart;
    // 모집 종료 기간
    private String receptionEnd;







}
