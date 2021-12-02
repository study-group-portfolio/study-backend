package kr.co.studit.entity;

import javax.persistence.*;

@Entity
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
    Skill skill;
}
