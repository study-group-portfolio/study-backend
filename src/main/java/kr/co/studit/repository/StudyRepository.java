package kr.co.studit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.entity.Study;
import kr.co.studit.entity.StudyTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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
}
