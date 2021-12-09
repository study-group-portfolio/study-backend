package kr.co.studit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static kr.co.studit.entity.QPosition.position;
import static kr.co.studit.entity.QRegion.region;
import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.QTool.tool;

@Repository
public class StudyRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    public StudyRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
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
}
