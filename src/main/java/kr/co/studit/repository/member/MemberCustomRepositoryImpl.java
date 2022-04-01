package kr.co.studit.repository.member;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.dto.member.ProfileForm;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.*;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.position.QPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.ListUtils;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static kr.co.studit.entity.QBookmark.bookmark;
import static kr.co.studit.entity.QPortfolio.portfolio;
import static kr.co.studit.entity.QRegion.*;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.member.QMember.member;
import static kr.co.studit.entity.member.QMemberInvitation.memberInvitation;
import static kr.co.studit.entity.member.QMemberPosition.memberPosition;
import static kr.co.studit.entity.member.QMemberRegion.memberRegion;
import static kr.co.studit.entity.member.QMemberSkill.memberSkill;
import static kr.co.studit.entity.position.QPosition.position;

@Slf4j
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
    public void deletePortpolio(Member member) {
        queryFactory
                .delete(portfolio)
                .where(portfolio.member.id.eq(member.getId()))
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
    public Page<Member> searchPageMember(Pageable pageable) {
        QueryResults<Member> results = queryFactory
                .select(member)
                .from(member)
                .where(member.publicProfile.eq(true)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdDate.desc())
                .fetchResults();
        List<Member> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Member> searchPageMember(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<Member> results = queryFactory
                .selectFrom(member).distinct()
                .innerJoin(member.regions, memberRegion)
                .innerJoin(member.positions, memberPosition)
                .innerJoin(member.skills, memberSkill)
//                .innerJoin(member.bookmarks, bookmark)
//                .leftJoin(member.regions, memberRegion)
//                .leftJoin(member.positions, memberPosition)
//                .leftJoin(member.skills, memberSkill)
//                .leftJoin(member.bookmarks, bookmark)
                .where(
                        member.publicProfile.eq(true),
                        studyTypeEq(condition.getStudyType()),
                        onOffStatusEq(condition.getOnOffStatus()),
                        areaEq(condition.getRegions()),
                        positionEq(condition.getPositions()),
                        skillEq(condition.getSkills())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdDate.desc())
                .fetchResults();
        List<Member> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<MemberInvitation> findMemberInvitationByEmail(String email) {

        return queryFactory
                .selectFrom(memberInvitation)
                .where(memberInvitation.member.email.eq(email))
                .fetch();
    }
    public boolean checkInviteMember(String email) {
        return queryFactory
                .selectFrom(memberInvitation)
                .where(memberInvitation.member.email.eq(email))
                .fetch()
                .size() >= 1;

    }

    @Override
    public void deleteMemberInvitationById(Long id) {
        queryFactory
                .delete(memberInvitation)
                .where(memberInvitation.id.eq(id))
                .execute();
    }

    @Override
    public MemberInvitation findMemberInvitationById(Long id) {
        return queryFactory
                .selectFrom(memberInvitation)
                .where(memberInvitation.id.eq(id))
                .fetchOne()
                ;
    }


    private BooleanExpression skillEq(List<String> skills) {
        return ListUtils.isEmpty(skills) ? null : memberSkill.skill.skillName.in(skills);
    }

    private BooleanExpression positionEq(List<String> positions) {
        return ListUtils.isEmpty(positions) ? null : memberPosition.position.positionName.in(positions);
    }

    private BooleanExpression areaEq(List<String> regions) {
        return ListUtils.isEmpty(regions) ? null : memberRegion.region.area.in(regions);
    }

    private BooleanExpression onOffStatusEq(OnOffStatus onOffStatus) {
        return StringUtils.isEmpty(onOffStatus.toString()) ? null : member.onOffStatus.eq(onOffStatus);
    }

    private BooleanExpression studyTypeEq(StudyType studyType) {
        log.info("info {}", StringUtils.isEmpty(studyType.toString()));
        return StringUtils.isEmpty(studyType.toString()) ? null :  member.studyType.eq(studyType);
    }



}
