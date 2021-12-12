package kr.co.studit.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.dto.mapper.StudySearchDto;
import kr.co.studit.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static kr.co.studit.entity.QPosition.position;
import static kr.co.studit.entity.QRegion.region;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.QStudy.study;
import static kr.co.studit.entity.QStudyPosition.studyPosition;
import static kr.co.studit.entity.QStudySkill.studySkill;
import static kr.co.studit.entity.QStudyTool.studyTool;
import static kr.co.studit.entity.QTool.tool;

@Repository
public class StudyRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    public StudyRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }

    public List<Skill> findSkill(Study study, Position position) {
        return queryFactory
                .select(skill)
                .from(studySkill)
                .where(
                        studySkill.study.id.eq(study.getId())
                                .and(studySkill.skill.position.positionName.eq(position.getPositionName())))
                .fetch();
    }

    public void createStudy(Study study) {
        em.persist(study);
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
        JPAQuery<Study> query = queryFactory
                .selectFrom(study)
                .where(
                        study.type.eq(searchDto.getType())
                        , study.onOffStatus.eq(searchDto.getStatus())
                        , study.region.area.eq(searchDto.getRegion())
                );
//                .leftJoin(study.studySkill, studySkill);

        for (String nowStudySkill : searchDto.getSkills()) {
            query.where(studySkill.skill.skillName.eq(nowStudySkill)).leftJoin(study.studySkill, studySkill);
        }
        return query.fetch();
    }
}
