package kr.co.studit.service;

import kr.co.studit.dto.PositionDto;
import kr.co.studit.dto.StudyDto;
import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.respository.StudyRepository;
import kr.co.studit.respository.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class StudyService {

    @Autowired
    StudyDataRepository studyDataRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    MemberDataRepository memberDataRepository;
    @Autowired
    RegionDataRepository regionDataRepository;
    @Autowired
    ToolDataRepository toolDataRepository;
    @Autowired
    PositionDataRepository positionDataRepository;
    @Autowired
    SkillDataRepository skillDataRepository;


    @Transactional
    public StudyDto createStudy(StudyDto studyDto) {

        Study study = mapperStudy(studyDto);
        try {


        } catch (Exception e) {
            return null;
        }
        return studyDto;
    }
    private Study mapperStudy(StudyDto studyDto) {
        Study study = new Study();

        Member member = memberDataRepository.findMemberByEmail(studyDto.getEmail());
        study.setMember(member);

        study.setTitle(studyDto.getTitle());
        study.setStudyDay(studyDto.getStudyDay());
        study.setContent(studyDto.getContent());
        study.setDuration(studyDto.getDuration());
        study.setReceptionStart(studyDto.getReceptionStart());
        study.setReceptionEnd(studyDto.getReceptionEnd());
        study.setOnOffStatus(studyDto.getStatus());
        if (studyDto.getStatus() != OnOffStatus.OFF) {
            createRegion(study, studyDto.getRegion());
        }
        study.setType(studyDto.getType());
        createStudyTools(study,studyDto.getTools());
        createPositionSkill(study, studyDto.getPositions());


        return study;
    }
    private void createPositionSkill(Study study, ArrayList<PositionDto> positions) {
        for (PositionDto positionDto : positions) {

            Position position = positionDataRepository.findPositionByPositionName(positionDto.getPosition());

            StudyPosition studyPosition = new StudyPosition();
            studyPosition.setPosition(position);
            studyPosition.setStudy(study);
            studyPosition.setTotalCount(positionDto.getCount());

            study.getStudyPosition().add(studyPosition);

            for (String skl : positionDto.getSkills()) {
                StudySkill studySkill = new StudySkill();
                Skill skill = skillDataRepository.findSkillBySkill(skl);
                studySkill.setSkill(skill);
                studySkill.setStudy(study);
                study.getStudySkill().add(studySkill);
            }
        }
    }
    public void createStudyTools(Study study,ArrayList<String> tools) {

        for (String t : tools) {

            Tool tool = toolDataRepository.findToolByTool(t);

            StudyTool studyTool = new StudyTool();
            studyTool.setStudy(study);
            studyTool.setTool(tool);

            studyRepository.createStudyTool(studyTool);
            study.getStudytool().add(studyTool);
        }
    }

    private void createRegion(Study study, String area) {
        Region region = regionDataRepository.findRegionByArea(area);
        study.setRegion(region);
    }

}
