package kr.co.studit.entity;

import kr.co.studit.entity.member.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    Position position;

    String message;

    public static StudyApplication createStudyApplication(Study study, Member member, Position position, String message) {

        StudyApplication studyApplication = new StudyApplication();
        studyApplication.setStudy(study);
        studyApplication.setMember(member);
        studyApplication.setPosition(position);
        studyApplication.setMessage(message);
        return studyApplication;
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getStudyApplication().add(this);
    }
}