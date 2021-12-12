package kr.co.studit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StudyPosition {
    @Id
    @GeneratedValue
    @Column(name = "study_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    Position position;

    // 현재 지원자수
    Integer count=0;

    // 총 지원수
    Integer totalCount=0;

    public void setStudy(Study study) {
        this.study = study;
        study.getStudyPosition().add(this);
    }

}
