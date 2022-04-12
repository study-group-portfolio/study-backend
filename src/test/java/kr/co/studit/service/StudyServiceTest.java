package kr.co.studit.service;

import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.study.Study;
import kr.co.studit.entity.study.StudyPosition;
import kr.co.studit.entity.study.StudySkill;
import kr.co.studit.entity.study.StudyTool;
import kr.co.studit.repository.study.StudyRepository;
import kr.co.studit.repository.study.*;
import kr.co.studit.repository.member.MemberDataRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(readOnly = true)
class StudyServiceTest {

    @Autowired
    MemberDataRepository memberDataRepository;

    @Autowired
    StudyDataRepository studyDataRepository;

    @Autowired
    StudyRepository studyRepository;



    @Autowired
    EntityManager em;

    @Test

    public void 스터디생성() throws Exception {
        //given
        String user = "test@test.com";
        Member findMember = memberDataRepository.findMemberByEmail(user);
        assertThat(findMember.getEmail()).isEqualTo(user);
        StudyDto studyDto = createStudyDto(user);
        // 만들어줘야하는 엔티티
        // Study
        // StudyTool
        // StudySkill
        Study study = createStudy(studyDto,findMember);
        createStudyTools(study,studyDto.getTools());
        createPositionSkill(study, studyDto.getPositions());
        createRegion(study, studyDto.getRegion());
        em.persist(study);
        em.flush();
        em.clear();

        Study findStudy = studyDataRepository.findStudyByMember(findMember).get(1);
        for (StudySkill studySkill : findStudy.getStudySkill()) {
            System.out.println(studySkill.getSkill().getSkillName());
        }
        for (StudyPosition studyPosition : findStudy.getStudyPosition()) {
            System.out.println("studyPosition = " + studyPosition.getPosition().getPositionName());
        }
        for (StudyTool tool : findStudy.getStudytool()) {
            System.out.println("tool = " + tool.getTool().getToolName());
        }
        System.out.println(study);


    }

    private void createPositionSkill(Study study, ArrayList<PositionDto> positions) {
        for (PositionDto positionDto : positions) {

            Position position = studyRepository.findPositionByPositionName(positionDto.getPosition());

            StudyPosition studyPosition = new StudyPosition();
            studyPosition.setPosition(position);
            studyPosition.setStudy(study);
            studyPosition.setTotalCount(positionDto.getCount());

            study.getStudyPosition().add(studyPosition);

            for (String skl : positionDto.getSkills()) {
                StudySkill studySkill = new StudySkill();
                Skill skill = studyRepository.findSkillBySkill(skl);
                studySkill.setSkill(skill);
                studySkill.setStudy(study);
                study.getStudySkill().add(studySkill);
            }
        }
    }

    private Study createStudy(StudyDto studyDto, Member member) {
        Study study = new Study();

        study.setTitle(studyDto.getTitle());
        study.setMember(member);
        study.setStudyDay(studyDto.getStudyDay());
        study.setContent(studyDto.getContent());
        study.setDuration(studyDto.getDuration());
        study.setReceptionStart(studyDto.getReceptionStart());
        study.setReceptionEnd(studyDto.getReceptionEnd());
        study.setOnOffStatus(studyDto.getStatus());
        study.setType(studyDto.getType());

        return study;
    }

    private void createRegion(Study study, String area) {
        Region region = studyRepository.findRegionByArea(area);
        study.setRegion(region);
    }


    public void createStudyTools(Study study,ArrayList<String> tools) {

        for (String t : tools) {

            Tool tool = studyRepository.findToolByToolName(t);

            StudyTool studyTool = new StudyTool();
            studyTool.setStudy(study);
            studyTool.setTool(tool);

            studyRepository.createStudyTool(studyTool);
            study.getStudytool().add(studyTool);
        }
    }

    private StudyDto createStudyDto(String email) {
        StudyDto studyDto = new StudyDto();


        studyDto.setType(StudyType.PROJECT);

        studyDto.setTitle("타이틀");

        studyDto.setContent("내용...");

        studyDto.setProfileShare(true);

        studyDto.setStatus(OnOffStatus.ON);

        studyDto.setRegion("서울");

        studyDto.setDuration("3달");

        studyDto.setStudyDay("주말");

        PositionDto position1 = new PositionDto();
        position1.setPosition("백엔드 개발자");
        position1.setCount(5);
        position1.getSkills().add("Spring");
        position1.getSkills().add("Java");

        PositionDto position2 = new PositionDto();
        position2.setPosition("프론트엔드 개발자");
        position2.setCount(4);
        position2.getSkills().add("React");
        position2.getSkills().add("Vue");

        studyDto.setPositions(new ArrayList<>());
        studyDto.getPositions().add(position1);
        studyDto.getPositions().add(position2);

        studyDto.setReceptionStart(LocalDate.now());
        studyDto.setReceptionEnd(LocalDate.now().plusDays(7));

        ArrayList<String> tools = new ArrayList<>();
        tools.add("Git");
        tools.add("Jira");

        studyDto.setTools(tools);

        return studyDto;
    }

    @Test
    public void positionRecruitedState() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("user0");

        List<Study> studyList = studyDataRepository.findStudyByMember(member);
        Study study = studyList.get(0);
        List<StudyPosition> studyPosition = study.getStudyPosition();
        for (StudyPosition position : studyPosition) {
            System.out.println("position.getCount() = " + position.getCount());
            System.out.println("position.getTotalCount() = " + position.getTotalCount());
            System.out.println("position.isRecruited() = " + position.isRecruited());
        }

    }

    @Test
    public void positionRecruitedStateTrue() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("user0");

        List<Study> studyList = studyDataRepository.findStudyByMember(member);
        Study study = studyList.get(0);
        List<StudyPosition> studyPosition = study.getStudyPosition();
        for (StudyPosition position : studyPosition) {
            if (position.isRecruited()) {
                System.out.println("해당 포지션에 모집이 완료 되었습니다.");
            }else {
                int addCount = 0;
                for (int i = position.getCount(); i <= position.getTotalCount(); i++) {
                    addCount = i;
                }
                position.setCount(addCount);

            }
            System.out.println("position.getStudy().getId() = " + position.getStudy().getId());
            System.out.println("position.getCount() = " + position.getCount());
            System.out.println("position.getTotalCount() = " + position.getTotalCount());
            System.out.println("position.isRecruited() = " + position.isRecruited());
        }

        List<Study> studyList2 = studyDataRepository.findStudyByMember(member);
        Study stud2 = studyList.get(0);
        List<StudyPosition> studyPosition2 = study.getStudyPosition();
        for (StudyPosition position : studyPosition2) {
            System.out.println("2===========================2");
            System.out.println("position.getCount() = " + position.getCount());
            System.out.println("position.getTotalCount() = " + position.getTotalCount());
            System.out.println("position.isRecruited() = " + position.isRecruited());
        }


        assertThat( studyDataRepository.findStudyByMember(member).get(0).getStudyPosition().get(0).isRecruited()).isTrue();
    }

    @Test
    public void applyStudy() throws Exception {
        //given 17
        Long studyId = 17L;


        //when

        //then

    }

}