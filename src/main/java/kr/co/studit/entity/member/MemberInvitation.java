package kr.co.studit.entity.member;

import kr.co.studit.entity.common.BaseTimeEntity;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.study.Study;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MemberInvitation extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name="member_invitation_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    // 방장이 초대한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    Position position;

    String message;

    public static MemberInvitation createStudyInvitation(Study study, Member member, Position position, String message) {

        MemberInvitation memberInvitation = new MemberInvitation();
        memberInvitation.setStudy(study);
        memberInvitation.setMember(member);
        memberInvitation.setPosition(position);
        memberInvitation.setMessage(message);
        return memberInvitation;
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getMemberInvitation().add(this);
    }
}
