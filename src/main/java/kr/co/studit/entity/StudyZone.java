package kr.co.studit.entity;

import javax.persistence.*;

@Entity
public class StudyZone {

    @Id
    @GeneratedValue
    @Column(name = "study_zone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Tool zone;
}