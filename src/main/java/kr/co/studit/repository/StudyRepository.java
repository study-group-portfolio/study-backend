package kr.co.studit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.dto.mapper.StudySearchDto;
import kr.co.studit.entity.*;
import kr.co.studit.repository.data.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static kr.co.studit.entity.QMember.member;
import static kr.co.studit.entity.QPosition.position;
import static kr.co.studit.entity.QRegion.region;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.QStudy.study;
import static kr.co.studit.entity.QStudyApplication.studyApplication;
import static kr.co.studit.entity.QStudyPosition.studyPosition;
import static kr.co.studit.entity.QStudySkill.studySkill;
import static kr.co.studit.entity.QStudyTool.studyTool;
import static kr.co.studit.entity.QTool.tool;

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
    @Transactional
    public void deletePosition(Study myStudy) {
        queryFactory
                .delete(studyPosition)
                .where(studyPosition.study.id.eq(myStudy.getId()))
                .execute();



    }
    @Transactional
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

    public List<Study> findStudyByFilter(StudySearchDto searchDto) {
        List<Study> studyList = queryFactory
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

                ).fetch();

        return studyList;
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

    public List<Study> findCreatedStudyByEmail(String email) {
        return queryFactory
                .selectFrom(study)
                .where(study.member.email.eq(email))
                .fetch();
    }

    public List<Study> findParticipatedStudyByEmail(String email) {
        return null;
    }

    public void deleteStudyApplicationById(Long id) {
        queryFactory
                .delete(studyApplication)
                .where(studyApplication.id.eq(id));

    }
    public StudyApplication findStudyApplicationById(Long id) {

        return queryFactory
                .selectFrom(studyApplication)
                .where(studyApplication.id.eq(id))
                .fetchOne();
    }

//    public StudyApplication saveStudyApplication()


}
