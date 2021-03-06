package kr.co.studit.util;

import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.Tool;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.position.PositionType;
import kr.co.studit.entity.study.Study;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.repository.study.StudyDataRepository;
import kr.co.studit.service.BookmarkService;
import kr.co.studit.service.MemberService;
import kr.co.studit.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class InitDb {


    private final InitService initService;
    private final MemberDataRepository memberDataRepository;
    private final DataSource dataSource;


    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws Exception {
        List<Member> members = memberDataRepository.findAll();

        if(members.isEmpty()){
            log.info("DB is empty run DB init.");
            ResourceDatabasePopulator resourceDatabasePopulator =
                    new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("data.sql") );
            resourceDatabasePopulator.execute(dataSource);
            initService.dbInit1();
        }else {
            log.info("DB is not empty");
        }

    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final MemberDataRepository memberDataRepository;
        private final MemberService memberService;
        private final StudyService studyService;
        private final RegionDataRepository regionDataRepository;
        private final PasswordEncoder passwordEncoder;
        private final BookmarkService bookmarkService;
        private final StudyDataRepository studyDataRepository;

        // ????????? ??????
        private Member initMember() {
            Member member1 = Member.createMember("test@test.com");
            member1.setRole(Role.ADMIN);
            member1.setPassword(passwordEncoder.encode("test"));
            Member member2 = Member.createMember("test2@test.com");
            member2.setPassword(passwordEncoder.encode("test"));
            Member member3 = Member.createMember("test3@test.com");
            member3.setPassword(passwordEncoder.encode("test"));
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            return member1;
        }

        private void createStudys() throws Exception {
            for (int i = 0; i < 15; i++) {
                Member member = memberDataRepository.findMemberByNickname("user" + i);
                StudyDto studyDto = createStudyDto();
                if (i < 5) {
                    studyDto.setTitle(studyDto.getTitle()+i);
                    studyDto.setRegion("??????");
                } else if (5 <= i && i < 10) {
                    studyDto.setTitle(studyDto.getTitle()+i);
                    studyDto.setRegion("??????");
                } else if (10 <= 15) {
                    studyDto.setTitle(studyDto.getTitle()+i);
                    studyDto.setType(StudyType.SHARE);
                    studyDto.setRegion("??????");
                }
                studyService.createStudy(studyDto,member.getEmail());

            }
        }

        private StudyDto createStudyDto() {
            StudyDto studyDto = new StudyDto();


            studyDto.setType(StudyType.PROJECT);

            studyDto.setTitle("?????????");

            studyDto.setContent("??????...");

            studyDto.setProfileShare(true);

            studyDto.setStatus(OnOffStatus.ON);

            studyDto.setRegion("??????");

            studyDto.setDuration("3???");

            studyDto.setStudyDay("??????");

            PositionDto position1 = new PositionDto();
            position1.setPositionName("????????? ?????????");
            position1.setTotalCount(5);
            position1.setCount(5);
            position1.getSkills().add("Java");
            position1.getSkills().add("Spring");

            PositionDto position2 = new PositionDto();
            position2.setPositionName("??????????????? ?????????");
            position2.setTotalCount(6);
            position2.setCount(3);
            position2.getSkills().add("React");
            position2.getSkills().add("Vue");

            studyDto.setPositions(new ArrayList<>());
            studyDto.getPositions().add(position1);
            studyDto.getPositions().add(position2);

            studyDto.setReceptionStart(LocalDate.now().toString());
            studyDto.setReceptionEnd(LocalDate.now().plusDays(7).toString());

            ArrayList<String> tools = new ArrayList<>();
            tools.add("Git");
            tools.add("Jira");

            studyDto.setTools(tools);

            return studyDto;
        }

        private void initMembers() {
            int i = 0;
            for (i = 0; i < 5; i++){
                //????????? ?????? ?????? ????????? ????????? ??????
                Member member = Member.builder()
                        .bio("user"+i+"?????????")
                        .email("studit"+i+"@studit.co.kr")
                        .password("{noop}12345678")
                        .role(Role.USER)
                        .onOffStatus(OnOffStatus.ON)
                        .publicProfile(true)
                        .studyType(StudyType.SHARE)
                        .nickname("user"+i)
                        .build();

                em.persist(member);
                em.flush();
                List<String> positions = new ArrayList<>();
                positions.add("????????? ?????????");
                memberService.updateMemberPosition(positions, member);
                List<String> regions = new ArrayList<>();
                regions.add("??????");
                memberService.updateMemberRegion(regions, member);
                List<String> skills = new ArrayList<>();
                skills.add("Java");
                memberService.updateMemberSkill(skills, member);
            }

            for (i = 5; i < 10; i++){
                Member member = Member.builder()
                        .bio("user"+i+"?????????")
                        .email("studit"+i+"@studit.co.kr")
                        .password("{noop}12345678")
                        .role(Role.USER.USER)
                        .onOffStatus(OnOffStatus.OFF)
                        .publicProfile(true)
                        .studyType(StudyType.PROJECT)
                        .nickname("user"+i)
                        .build();

                em.persist(member);
                em.flush();

                List<String> positions = new ArrayList<>();
                positions.add("??????????????? ?????????");
                memberService.updateMemberPosition(positions, member);
                List<String> regions = new ArrayList<>();
                regions.add("??????");
                regions.add("??????");
                memberService.updateMemberRegion(regions, member);
                List<String> skills = new ArrayList<>();
                skills.add("React");
                skills.add("Vue");
                memberService.updateMemberSkill(skills, member);
            }

            for (i = 10; i < 15; i++){
                //???????????? ????????? ??????x ?????????  ????????????
                Member member = Member.builder()
                        .bio("user"+i+"?????????")
                        .email("studit"+i+"@studit.co.kr")
                        .password("{noop}12345678")
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
                positions.add("????????? ?????????");
                memberService.updateMemberPosition(positions, member);
               /* List<String> regions = new ArrayList<>();
                regions.add("??????");
                memberService.updateMemberRegion(regions, member);*/
                List<String> skills = new ArrayList<>();
                skills.add("Spring");
                skills.add("C++");
                memberService.updateMemberSkill(skills, member);
            }


        }

        private void initRegion() {
            String[] areas = {"??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????"};
            List<Region> regions = Arrays.stream(areas).map(s -> Region.createRegion(s)).collect(Collectors.toList());

            em.persist(regions);
        }

        private void initPositionAndSkill() {
            PositionType positionTypeBack = PositionType.createPostionType("??????");
            PositionType positionTypeDesign = PositionType.createPostionType("?????????");
            PositionType positionTypePlan = PositionType.createPostionType("??????");


            Position position1 = Position.createPostion("????????? ?????????");
            position1.setPositionType(positionTypeBack);


            Skill skillBack1 = Skill.createSkill("Spring");
            skillBack1.setPosition(position1);

            Skill skillBack2 = Skill.createSkill("Java");
            skillBack2.setPosition(position1);

            Skill skillBack3 = Skill.createSkill("Node.js");
            skillBack3.setPosition(position1);



            position1.getSkills().add(skillBack1);
            position1.getSkills().add(skillBack2);
            position1.getSkills().add(skillBack3);


            Position position2 = Position.createPostion("??????????????? ?????????");
            position2.setPositionType(positionTypeBack);

            Skill skillFront1 = Skill.createSkill("React");
            skillFront1.setPosition(position2);

            Skill skillFront2 = Skill.createSkill("Vue");
            skillFront2.setPosition(position2);

            Skill skillFront3 = Skill.createSkill("?????????");
            skillFront3.setPosition(position2);

            em.persist(positionTypeBack);
            em.persist(positionTypeDesign);
            em.persist(positionTypePlan);
        }

        private void initToll() {
            Tool tool1 = Tool.createTool("Git");
            Tool tool2 = Tool.createTool("Jira");

            em.persist(tool1);
            em.persist(tool2);
        }

        private void initStudy(Member member) {
            Region zone = regionDataRepository.findRegionByArea("??????");
            Study study = Study.createStudy(member,zone);
            study.setTitle("???????????????");
            em.persist(study);
        }

        private void initBookmarkStudyList() {
            Member admin = memberDataRepository.findMemberByNickname("admin");
            Long id = admin.getId();
            List<Study> studies = studyDataRepository.findAll();
            int i =0;
            while (studies.iterator().hasNext() && i <= 5){
                bookmarkService.createStudyBookmark(admin,studies.get(i));
                i++;
            }
//            for (Study study: studies) {
//                bookmarkService.createStudyBookmark(admin,study);
//            }


        }

        private void createStudy() {
            memberDataRepository.findMemberByNickname("user1");
        }

        public void dbInit1() throws Exception {
            Member member = initMember();
//            Region zone = initRegion();
//            initPositionAndSkill();
//            initToll();
//            initStudy(member);
            initMembers();
            createStudys();
            initBookmarkStudyList();
            em.flush();
//            em.clear();
//            List<Study> list = member.getStudys();

        }

    }
}
