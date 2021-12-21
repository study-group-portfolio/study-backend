package kr.co.studit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StudyApplication {
    @Id
    @GeneratedValue
    @Column(name="study_application_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    Position position;

    String message;
}
