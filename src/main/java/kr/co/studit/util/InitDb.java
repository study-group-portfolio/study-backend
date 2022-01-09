package kr.co.studit.util;

import kr.co.studit.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {


    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
//        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            Member member1 = Member.createMember("test@test.com");
            member1.setEmail("test@test.com");
            Member member2 = Member.createMember("test2@test.com");
            em.persist(member1);
            em.persist(member2);



            Region zone1 = Region.createRegion("서울");
            Region zone2 = Region.createRegion("부산");
            em.persist(zone1);
            em.persist(zone2);


            Position position1 = Position.createPostion("백엔드");
            em.persist(position1);

            Skill skillBack1 = Skill.createSkill("스프링");
            skillBack1.setPosition(position1);

            Skill skillBack2 = Skill.createSkill("장고");
            skillBack2.setPosition(position1);

            Skill skillBack3 = Skill.createSkill("노드");
            skillBack3.setPosition(position1);

            em.persist(skillBack1);
            em.persist(skillBack2);
            em.persist(skillBack3);

            position1.getSkills().add(skillBack1);
            position1.getSkills().add(skillBack2);
            position1.getSkills().add(skillBack3);


            Position position2 = Position.createPostion("프론트");
            em.persist(position2);

            Skill skillFront1 = Skill.createSkill("리엑트");
            skillFront1.setPosition(position2);

            Skill skillFront2 = Skill.createSkill("뷰");
            skillFront2.setPosition(position2);

            Skill skillFront3 = Skill.createSkill("앵귤러");
            skillFront3.setPosition(position2);

            em.persist(skillFront1);
            em.persist(skillFront2);
            em.persist(skillFront3);

            Tool tool1 = Tool.createTool("Git");
            Tool tool2 = Tool.createTool("Jira");

            em.persist(tool1);
            em.persist(tool2);

            Study study = Study.createStudy(member1,zone1);
            study.setTitle("타이틀이요");
            em.persist(study);




            em.flush();
//            em.clear();
//            List<Study> list = member.getStudys();




        }

    }
}
