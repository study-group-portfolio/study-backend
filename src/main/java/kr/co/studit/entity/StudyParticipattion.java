package kr.co.studit.entity;

import kr.co.studit.entity.member.Member;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class StudyParticipattion {
    @Id
    @GeneratedValue
    @Column(name="study_participation_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    Position position;

    public static StudyParticipattion createStudyParticipattion(Study study, Member member, Position position) {

        StudyParticipattion studyParticipattion = new StudyParticipattion();
        studyParticipattion.setStudy(study);
        studyParticipattion.setMember(member);
        studyParticipattion.setPosition(position);
        return studyParticipattion;
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getStudyParticipattion().add(this);
    }
}
