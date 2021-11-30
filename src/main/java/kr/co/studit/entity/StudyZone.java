package kr.co.studit.entity;

import javax.persistence.*;

@Entity
public class StudyZone {

    @Id
    @GeneratedValue
    @Column(name = "study_zone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    Tool zone;
}
