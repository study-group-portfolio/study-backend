package kr.co.studit.repository.member;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.dto.ProfileForm;
import kr.co.studit.dto.QProfileForm;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.member.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.list;
import static kr.co.studit.entity.member.QMember.member;
import static kr.co.studit.entity.member.QMemberRegion.memberRegion;
import static kr.co.studit.entity.QPosition.position;
import static kr.co.studit.entity.member.QMemberPosition.memberPosition;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.member.QMemberSkill.memberSkill;
@Repository
@RequiredArgsConstructor
//커스텀 인터페이스 쿼리 구현
public class MemberCustomRepositoryImpl implements MemberCustomRepository {


    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Long editProfile(ProfileForm profileForm) {
        return queryFactory
                .update(member)
                .set(member.bio, profileForm.getBio())
                .set(member.onOffStatus, profileForm.getOnOffStatus())
                .where(member.nickname.eq(profileForm.getNickname())).execute();
    }

    @Override
    public void delateRegions(Member member) {
        queryFactory
                .delete(memberRegion)
                .where(memberRegion.member.id.eq(member.getId()))
                .execute();
    }

    @Override
    public void saveMemberRegion(MemberRegion memberRegion) {
        em.persist(memberRegion);
    }

    @Override
    public Position findPositionByPositionName(String positionName) {
        return queryFactory
                .selectFrom(position)
                .where(position.positionName.eq(positionName))
                .fetchOne();
    }

    @Override
    public void saveMemberPosition(MemberPosition memberPosition) {
        em.persist(memberPosition);
    }

    @Override
    public void deletePosition(Member member) {
        queryFactory
                .delete(memberPosition)
                .where(memberPosition.member.id.eq(member.getId()))
                .execute();
    }

    @Override
    public List<Skill> findSkillBySkillName(List<String> skills) {
        return queryFactory
                .selectFrom(skill)
                .where(skill.skillName.in(skills))
                .fetch();
    }

    @Override
    public void saveMemberSkill(MemberSkill memberSkill) {
        em.persist(memberSkill);
    }

    @Override
    public void deleteSkill(Member member) {
        queryFactory
                .delete(memberSkill)
                .where(memberSkill.member.id.eq(member.getId()))
                .execute();
    }

    @Override
    public ProfileForm findProfileFormByMemberId(Long id) {
       return queryFactory
                .select(Projections.fields(ProfileForm.class,
                        member.nickname,
                        member.bio,
                        member.onOffStatus,
                        member.email,
                        member.studyType,
                        ExpressionUtils.as(JPAExpressions.select(memberRegion.region.area)
                                            .from(memberRegion)
                                            .where(memberRegion.member.id.eq(id)),"regions"))
                        )
                .from(member)
                .where(member.id.eq(id))
                .fetchOne();


    }
}
