package kr.co.studit.service;

import kr.co.studit.dto.*;
import kr.co.studit.dto.mapper.StudyMapperDto;
import kr.co.studit.dto.mapper.StudySearchDto;
import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.mapper.StudyMapper;
import kr.co.studit.repository.StudyRepository;
import kr.co.studit.repository.data.*;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class StudyService {

    @Autowired
    StudyDataRepository studyDataRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    MemberDataRepository memberDataRepository;

    @Autowired
    SqlSession sqlSession;



    @Transactional
    public StudyDto createStudy(StudyDto studyDto) {

        Study study = studyMapper(studyDto);
        Study saveStudy = studyDataRepository.save(study);
        StudyDto saveStudyDto = studyDtoMapper(study);

        return studyDto;
    }

    private void createPositionSkill(Study study, ArrayList<PositionDto> positions) {
        for (PositionDto positionDto : positions) {

            Position position = studyRepository.findPositionByPositionName(positionDto.getPosition());

            StudyPosition studyPosition = new StudyPosition();
            studyPosition.setPosition(position);
            studyPosition.setStudy(study);
            studyPosition.setCount(positionDto.getCount());
            studyPosition.setTotalCount(positionDto.getTotalCount());

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
    public void createStudyTools(Study study,ArrayList<String> tools) {

        for (String t : tools) {

            Tool tool = studyRepository.findToolByToolName(t);

            StudyTool studyTool = new StudyTool();
            studyTool.setStudy(study);
            studyTool.setTool(tool);

        }
    }

    private void createRegion(Study study, String area) {
        Region region = studyRepository.findRegionByArea(area);
        study.setRegion(region);
    }

    public List<StudyDto> findStudies() {
        List<Study> allStudy = studyDataRepository.findAll();
        List<StudyDto> list = new ArrayList<>();
        for (Study study : allStudy) {
            list.add(studyDtoMapper(study));
        }
        return list;
    }

    private StudyDto studyDtoMapper(Study study) {
        StudyDto studyDto = new StudyDto();
        studyDto.setId(study.getId());
        studyDto.setType(study.getType());
        studyDto.setTitle(study.getTitle());
        studyDto.setContent(study.getContent());
        studyDto.setStatus(study.getOnOffStatus());
        if (study.getOnOffStatus() != OnOffStatus.OFF) {
            studyDto.setRegion(study.getRegion().getArea());
        }
        studyDto.setDuration(study.getDuration());
        studyDto.setStudyDay(study.getStudyDay());

        studyDto.setReceptionStart(study.getReceptionStart());
        studyDto.setReceptionEnd(study.getReceptionEnd());
        studyDto.setTools(new ArrayList<>());
        List<StudyTool> studyTools = study.getStudytool();
        for (StudyTool studyTool : studyTools) {
            Tool tool = studyTool.getTool();
            String toolName = tool.getToolName();
            studyDto.getTools().add(toolName);
        }

        List<StudyPosition> studyPositions = study.getStudyPosition();
        for (StudyPosition studyPosition : studyPositions) {
            PositionDto positionDto = new PositionDto();

            Position position = studyPosition.getPosition();
            positionDto.setPosition(position.getPositionName());
            positionDto.setCount(studyPosition.getCount());
            positionDto.setTotalCount(studyPosition.getTotalCount());

            List<Skill> skills = studyRepository.findSkill(study, position);
            for (Skill skill : skills) {
                positionDto.getSkills().add(skill.getSkillName());
            }
            studyDto.getPositions().add(positionDto);

        }
        return studyDto;
    }


    private Study studyMapper(StudyForm studyDto) {
        Study study = new Study();
        if (studyDto.getId() != null) {
            study.setId(studyDto.getId());
        }
        Member member = memberDataRepository.findMemberByEmail(studyDto.getEmail());
        study.setMember(member);

        studySetData(study,studyDto,false);


        return study;
    }

    private void studySetData(Study study,StudyForm studyDto,boolean update) {
        if (update == true) {
            study.setRegion(null);
            studyRepository.deletePosition(study);
            studyRepository.deleteSkill(study);
            studyRepository.deleteTool(study);
        }

        study.setType(studyDto.getType());
        study.setTitle(studyDto.getTitle());
        study.setContent(studyDto.getContent());

        study.setOnOffStatus(studyDto.getStatus());
        study.setRegion(null);
        if (studyDto.getStatus() != OnOffStatus.OFF) {
            createRegion(study, studyDto.getRegion());
        }
        study.setDuration(studyDto.getDuration());
        study.setStudyDay(studyDto.getStudyDay());


        study.setReceptionStart(studyDto.getReceptionStart());
        study.setReceptionEnd(studyDto.getReceptionEnd());

        study.setStudySkill(new ArrayList<StudySkill>());
        study.setStudyPosition(new ArrayList<StudyPosition>());
        createStudyTools(study, studyDto.getTools());
        createPositionSkill(study, studyDto.getPositions());


    }

    public StudyDto findStudy(Long id) throws Exception{
        Optional<Study> optionalStudy = studyDataRepository.findById(id);
        if(optionalStudy.isEmpty()){
            throw new Exception();

        }
        Study study = optionalStudy.get();
        StudyDto studyDto = studyDtoMapper(study);
        return studyDto;
    }

    public void deleteStudy(Long id) {
        studyDataRepository.deleteById(id);

    }

    public void updateStudy(Long id, StudyUpdateDto studyUpdateDto) throws Exception {
        Optional<Study> optionalStudy = studyDataRepository.findById(id);
        if(optionalStudy.isEmpty()){
            throw new Exception();
        }
        Study study = optionalStudy.get();
        studySetData(study,studyUpdateDto,true);

        studyDataRepository.save(study);

    }

    public void modifyStudy(Study study, StudyUpdateDto studyUpdateDto) {

    }

    public List<StudyDto> searchStudy(StudySearchDto searchDto) throws SQLException {
        List<StudyDto> studyDtoList = new ArrayList<>();
        HashMap<String, String> filterMap = new HashMap<>();

//        List<Study> studyList = studyRepository.findStudyByFilter(searchDto);
//        for (Study study : studyList) {
//            StudyDto studyDto = studyDtoMapper(study);
//            studyDtoList.add(studyDto);
//        }
        List<StudyMapperDto> studyMapperDtos = sqlSession.getMapper(StudyMapper.class).studyFilterSearch(searchDto);

        return studyDtoList;
    }

}
