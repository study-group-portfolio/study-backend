package kr.co.studit.entity.member;

import kr.co.studit.entity.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MemberSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    public static MemberSkill createMemberSkill(Skill skill, Member member) {
        MemberSkill memberSkill = new MemberSkill();
        memberSkill.setSkill(skill);
        memberSkill.setMember(member);
        member.addSkill(memberSkill);
        return memberSkill;
    }
}
