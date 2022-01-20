package kr.co.studit.entity.member;

import kr.co.studit.entity.Region;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MemberRegion {


    @Id
    @GeneratedValue
    @Column(name = "member_region_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    public static MemberRegion createMemberRegion(Member member, Region region) {
        MemberRegion memberRegion = new MemberRegion();
        memberRegion.setRegion(region);
        memberRegion.setMember(member);
        member.addRegions(memberRegion);
        return memberRegion;
    }

}
