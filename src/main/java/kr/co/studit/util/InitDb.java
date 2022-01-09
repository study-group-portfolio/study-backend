package kr.co.studit.util;

import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.member.MemberPosition;
import kr.co.studit.entity.member.MemberRegion;
import kr.co.studit.entity.member.MemberSkill;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        private final MemberDataRepository memberDataRepository;
        private final MemberService memberService;
        private final PasswordEncoder passwordEncoder;

        // 메소드 분리
        private Member initMember() {
            Member member = Member.createMember("test@test.com");
            em.persist(member);
            return member;
        }

        private void serPosition() {

        }

        private void initMembers() {
            int i = 0;
            for (i = 0; i < 30; i++){
                //온라인 지역 서울 백엔드 스프링 공유
                Member member = Member.builder()
                        .bio("user"+i+"입니다")
                        .email("studit"+i+"@studit.co.kr")
                        .password(passwordEncoder.encode("12345678"))
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .role(Role.USER)
                        .onOffStatus(OnOffStatus.ON)
                        .publicProfile(true)
                        .studyType(StudyType.SHARE)
                        .nickname("user"+i)
                        .build();

                em.persist(member);
                em.flush();
                List<String> positions = new ArrayList<>();
                positions.add("백엔드");
                memberService.updateMemberPosition(positions, member);
                List<String> regions = new ArrayList<>();
                regions.add("서울");
                memberService.updateMemberRegion(regions, member);
                List<String> skills = new ArrayList<>();
                skills.add("스프링");
                memberService.updateMemberSkill(skills, member);
            }

            for (i = 30; i < 60; i++){
                //오프라인 지역 대전 프론트 리액트 프로젝트
                Member member = Member.builder()
                        .bio("user"+i+"입니다")
                        .email("studit"+i+"@studit.co.kr")
                        .password("12345678")
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .role(Role.USER)
                        .onOffStatus(OnOffStatus.OFF)
                        .publicProfile(true)
                        .studyType(StudyType.PROJECT)
                        .nickname("user"+i)
                        .build();

                em.persist(member);
                em.flush();

                List<String> positions = new ArrayList<>();
                positions.add("프론트");
                memberService.updateMemberPosition(positions, member);
                List<String> regions = new ArrayList<>();
                regions.add("대전");
                regions.add("서울");
                memberService.updateMemberRegion(regions, member);
                List<String> skills = new ArrayList<>();
                skills.add("리엑트");
                skills.add("뷰");
                memberService.updateMemberSkill(skills, member);
            }

            for (i = 60; i < 100; i++){
                //오프라인 온오프 지역x 백엔드  프로젝트
                Member member = Member.builder()
                        .bio("user"+i+"입니다")
                        .email("studit"+i+"@studit.co.kr")
                        .password("12345678")
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .role(Role.USER)
                        .onOffStatus(OnOffStatus.ONOFF)
                        .publicProfile(true)
                        .studyType(StudyType.PROJECT)
                        .nickname("user"+i)
                        .build();
                em.persist(member);
                em.flush();

                em.persist(member);
                em.flush();
                List<String> positions = new ArrayList<>();
                positions.add("백엔드");
                memberService.updateMemberPosition(positions, member);
               /* List<String> regions = new ArrayList<>();
                regions.add("서울");
                memberService.updateMemberRegion(regions, member);*/
                List<String> skills = new ArrayList<>();
                skills.add("스프링");
                skills.add("장고");
                memberService.updateMemberSkill(skills, member);
            }


        }

        private Region initRegion() {
            Region zone1 = Region.createRegion("서울");
            Region zone2 = Region.createRegion("부산");
            Region zone3 = Region.createRegion("대전");
            Region zone4 = Region.createRegion("대구");
            em.persist(zone1);
            em.persist(zone2);
            em.persist(zone3);
            em.persist(zone4);
            return zone1;
        }

        private void initPositionAndSkill() {
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
        }

        private void initToll() {
            Tool tool1 = Tool.createTool("Git");
            Tool tool2 = Tool.createTool("Jira");

            em.persist(tool1);
            em.persist(tool2);
        }

        private void initStudy(Member member, Region zone) {
            Study study = Study.createStudy(member,zone);
            study.setTitle("타이틀이요");
            em.persist(study);
        }

        public void dbInit1() {
            Member member = initMember();
            Region zone = initRegion();
            initPositionAndSkill();
            initToll();
            initStudy(member, zone);
            initMembers();
            em.flush();
//            em.clear();
//            List<Study> list = member.getStudys();

        }

    }
}
