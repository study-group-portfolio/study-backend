package kr.co.studit.repository.member;

import kr.co.studit.dto.member.ProfileForm;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.member.MemberPosition;
import kr.co.studit.entity.member.MemberRegion;
import kr.co.studit.entity.member.MemberSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//커스텀 인터페이스 쿼리 작성
public interface MemberCustomRepository {

    Long editProfile(ProfileForm profileForm);

    void delateRegions(Member member);

    void saveMemberRegion(MemberRegion memberRegion);

    Position findPositionByPositionName(String positionName);

    void saveMemberPosition(MemberPosition memberPosition);

    void deletePosition(Member member);

    List<Skill> findSkillBySkillName(List<String> skills);

    void saveMemberSkill(MemberSkill memberSkill);

    void deleteSkill(Member member);

    Page<Member> searchPageMember(Pageable pageable);

    Page<Member> searchPageMember(MemberSearchCondition condition, Pageable pageable);
}
