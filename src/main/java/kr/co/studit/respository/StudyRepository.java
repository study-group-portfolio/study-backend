package kr.co.studit.respository;

import kr.co.studit.entity.Study;
import kr.co.studit.entity.StudyTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
public class StudyRepository {
    @Autowired
    EntityManager em;


    public void createStudy(Study study) {
        em.persist(study);
    }

    public void createStudyTool(StudyTool studyTool) {
        em.persist(studyTool);

    }
}
