package kr.co.studit.member.domain;

import kr.co.studit.position.domain.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MemberPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    public static MemberPosition createMemberPosition(Position findPositon, Member member) {
        MemberPosition memberPosition = new MemberPosition();
        memberPosition.setPosition(findPositon);
        memberPosition.setMember(member);
        member.addPositon(memberPosition);
        return memberPosition;
    }
}
