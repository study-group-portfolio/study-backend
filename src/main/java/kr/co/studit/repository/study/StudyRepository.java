package kr.co.studit.repository.study;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.dto.search.StudySearchCondition;
import kr.co.studit.entity.*;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.position.QPosition;
import kr.co.studit.entity.study.*;
import kr.co.studit.repository.member.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static kr.co.studit.entity.QRegion.region;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.position.QPosition.position;
import static kr.co.studit.entity.study.QStudy.study;
import static kr.co.studit.entity.study.QStudyApplication.studyApplication;
import static kr.co.studit.entity.study.QStudyParticipation.studyParticipation;
import static kr.co.studit.entity.study.QStudyPosition.studyPosition;
import static kr.co.studit.entity.study.QStudySkill.studySkill;
import static kr.co.studit.entity.study.QStudyTool.studyTool;
import static kr.co.studit.entity.QTool.tool;
import static kr.co.studit.entity.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class StudyRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final MemberDataRepository memberDataRepository;



    public List<Skill> findSkill(Study study, Position position) {
        return queryFactory
                .select(skill)
                .from(studySkill)
                .where(
                        studySkill.study.id.eq(study.getId())
                                .and(studySkill.skill.position.positionName.eq(position.getPositionName())))
                .fetch();
    }

    public void createStudyTool(StudyTool studyTool) {
        em.persist(studyTool);
    }

    public Tool findToolByToolName(String toolName) {
        return queryFactory
                .selectFrom(tool)
                .where(tool.toolName.eq(toolName))
                .fetchOne();
    }

    public Position findPositionByPositionName(String positionName){
        return queryFactory
                .selectFrom(position)
                .where(position.positionName.eq(positionName))
                .fetchOne();
    }
    public Region findRegionByArea(String area){
        return queryFactory
                .selectFrom(region)
                .where(region.area.eq(area))
                .fetchOne();
    }
    public Skill findSkillBySkill(String skillName) {
        return queryFactory
                .selectFrom(skill)
                .where(skill.skillName.eq(skillName))
                .fetchOne();

    }

//    @Transactional
//    public void deleteRegion() {
//        queryFactory
//                .delete(study.region)
//                .execute();
//    }
    public void deletePosition(Study myStudy) {
        queryFactory
                .delete(studyPosition)
                .where(studyPosition.study.id.eq(myStudy.getId()))
                .execute();



    }
    public void deleteSkill(Study myStudy) {
        queryFactory
                .delete(studySkill)
                .where(studySkill.study.id.eq(myStudy.getId()))
                .execute();
    }

    public void deleteTool(Study myStudy) {
        queryFactory
                .delete(studyTool)
                .where(studyTool.study.id.eq(myStudy.getId()))
                .execute();
    }

    public Page<Study> findStudyByFilter(StudySearchCondition searchDto,Pageable pageable) {
        QueryResults<Study> results = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyPosition, studyPosition)
                .leftJoin(studyPosition.position, position)
                .leftJoin(study.studySkill, studySkill)
                .leftJoin(studySkill.skill, skill)
                .where(
                        study.type.eq(searchDto.getType())
                        , study.onOffStatus.eq(searchDto.getStatus())
                        , study.region.area.eq(searchDto.getRegion())
                                .and(positionEmpty(searchDto.getPositions()))
                                .and(skillEmpty(searchDto.getSkills()))

                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Study> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);

    }

    public List<StudyApplication> findStudyApplicationByEmail(String email) {
        Member findMember = memberDataRepository.findMemberByEmail(email);
        List<StudyApplication> result = queryFactory
                .selectFrom(studyApplication)
                .leftJoin(studyApplication.study.member, member)
                .where(studyApplication.study.member.eq(findMember))
                .fetch();
        return result;

    }

    private BooleanExpression positionEmpty(ArrayList<String> positionNames) {
        if (positionNames == null || positionNames.isEmpty()) return null;
        return position.positionName.in(positionNames);
    }
    private BooleanExpression skillEmpty(ArrayList<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) return null;
        return skill.skillName.in(skillNames);
    }

    public void flush() {
        em.flush();
    }

    public StudyPosition findStudyPositionByName(Long studyId, String position) {
        return queryFactory
                .selectFrom(studyPosition)
                .where(studyPosition.study.id.eq(studyId), studyPosition.position.positionName.eq(position))
                .fetchOne();
    }

    public Page<Study> findCreatedStudyByEmail(String email,Pageable pageable) {
        QueryResults<Study> results = queryFactory
                .selectFrom(study)
                .where(study.member.email.eq(email))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Study> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);

    }



    public void deleteStudyApplicationById(Long id) {
        queryFactory
                .delete(studyApplication)
                .where(studyApplication.id.eq(id))
                .execute();

    }
    public StudyApplication findStudyApplicationById(Long id) {

        return queryFactory
                .selectFrom(studyApplication)
                .where(studyApplication.id.eq(id))
                .fetchOne();
    }

    public Page<Study> findParticipatedStudyByEmail(String email, Pageable pageable) {
        QueryResults<Study> results = queryFactory
                .select(studyParticipation.study)
                .from(studyParticipation)
                .where(studyParticipation.member.email.eq(email))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Study> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    public boolean checkApplyStudy(Member findMember){
        return queryFactory
                .select(studyApplication.member)
                .from(studyApplication)
                .where(studyApplication.member.eq(findMember))
                .fetch()
                .size() >= 1;
    }
    public boolean checkParticipateStudy(String email){
        return queryFactory
                .select(studyParticipation.member)
                .from(studyParticipation)
                .where(studyParticipation.member.email.eq(email))
                .fetch()
                .size() >= 1;
    }

    public Page<Study> findStudy(Pageable pageable) {
        QueryResults<Study> results = queryFactory
                .selectFrom(study)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Study> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);

    }

    public Optional<Study> findStudyByEmailAndId(Long studyId, String email) {
        Study result = queryFactory
                .selectFrom(study)
                .where(study.member.email.eq(email), QStudy.study.id.eq(studyId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<StudyPosition> findStudyPositionByStudyId(Long id) {
        QueryResults<StudyPosition> results = queryFactory
                .selectFrom(studyPosition)
                .where(studyPosition.study.id.eq(id))
                .fetchResults();
        List<StudyPosition> studyPositions = results.getResults();

        return studyPositions;
    }


//    public StudyApplication saveStudyApplication()


}
