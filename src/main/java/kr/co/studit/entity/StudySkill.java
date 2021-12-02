package kr.co.studit.entity;

import javax.persistence.*;

@Entity
public class StudySkill {

    @Id
    @GeneratedValue
    @Column(name = "study_skill_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    Skill skill;
}
