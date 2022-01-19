package kr.co.studit.service;

import kr.co.studit.dto.*;
import kr.co.studit.dto.enums.InviteType;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.position.PositionApplyDto;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.response.ResponseListDto;
import kr.co.studit.dto.search.StudySearchCondition;
import kr.co.studit.dto.study.StudyAllowDto;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.dto.study.StudyForm;
import kr.co.studit.dto.study.StudyUpdateDto;
import kr.co.studit.entity.*;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.member.MemberInvitation;
import kr.co.studit.entity.study.*;
import kr.co.studit.error.ErrorResponse;
import kr.co.studit.repository.study.StudyRepository;
import kr.co.studit.repository.study.*;
import kr.co.studit.repository.member.MemberDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class StudyService {

    private final StudyDataRepository studyDataRepository;
    private final StudyRepository studyRepository;
    private final MemberDataRepository memberDataRepository;
//    private final SqlSession sqlSession;

    public ResponseEntity<?> createStudy(StudyDto studyDto, String email) {
        ResponseDto<StudyDto> response = new ResponseDto<>();
        try {
            Study study = studyMapper(studyDto,email);
            Study saveStudy = studyDataRepository.save(study);
            StudyDto saveStudyDto = studyDtoMapper(saveStudy);
            response.setData(saveStudyDto);
            response.setStatus(Status.SUCCESS);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }



    }

    private void createPositionSkill(Study study, ArrayList<PositionDto> positions) {
        for (PositionDto positionDto : positions) {

            Position position = studyRepository.findPositionByPositionName(positionDto.getPosition());

            StudyPosition studyPosition = new StudyPosition();
            studyPosition.setPosition(position);
            studyPosition.setStudy(study);
            studyPosition.setCount(positionDto.getCount());
            studyPosition.setTotalCount(positionDto.getTotalCount());

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

    public ResponseEntity<?> findStudies(Pageable pageable) {
        ResponseListDto<StudyDto> response = new ResponseListDto<>();
        try {
            List<Study> findStudis = studyRepository.findStudy(pageable);
            List<StudyDto> list = new ArrayList<>();
            for (Study study : findStudis) {
                list.add(studyDtoMapper(study));
            }

            response.setStatus(Status.SUCCESS);
            response.setData(list);

            return new ResponseEntity<ResponseListDto<StudyDto>>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }



    }
    // bookmark에서 사용하기 위해 private -> public으로 변경 - jinu
    public StudyDto studyDtoMapper(Study study) {
        StudyDto studyDto = new StudyDto();
        studyDto.setId(study.getId());
        studyDto.setType(study.getType());
        studyDto.setTitle(study.getTitle());
        studyDto.setContent(study.getContent());
        studyDto.setStatus(study.getOnOffStatus());
        studyDto.setCreateDate(study.getCreatedDate());
        studyDto.setModifiedDate(study.getModifiedDate());
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


    private Study studyMapper(StudyForm studyDto, String email) {
        Study study = new Study();
        if (studyDto.getId() != null) {
            study.setId(studyDto.getId());
        }
        Member member = memberDataRepository.findMemberByEmail(email);
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

    public ResponseEntity<?> findStudy(Long id){
        ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
        try {
            Study study = studyDataRepository.findById(id).orElseThrow(() -> new Exception("존재하지 않는 스터디 입니다"));
            StudyDto studyDto = studyDtoMapper(study);
            response.setStatus(Status.SUCCESS);
            response.setData(studyDto);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }


    }

    public ResponseEntity<?> deleteStudy(Long id,String email) {
        ResponseDto<String> response = new ResponseDto<String>();
        try {
            Study study = studyDataRepository.findById(id).orElseThrow(() -> new Exception("해당 글이 없습니다"));
            if (study.getMember().getEmail().equals(email)) {
                studyDataRepository.deleteById(id);
                response.setData("정상 삭제됐습니다.");
                response.setStatus(Status.SUCCESS);
            }
            else{
                response.setData("스터디 글 생성자가 아닙니다.");
                response.setStatus(Status.FALSE);
            }
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(new Exception("해당 글이 존재하지 않습니다"));
        }


    }

    @Transactional
    public ResponseEntity<?> updateStudy(Long id, String email, StudyUpdateDto studyUpdateDto) {
        ResponseDto<String> response = new ResponseDto<String>();
        try {
            Study study = studyDataRepository.findById(id).orElseThrow(() -> new Exception("해당 글이 없습니다"));

            studySetData(study, studyUpdateDto, true);

            studyDataRepository.save(study);

            response.setStatus(Status.SUCCESS);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }


    public ResponseEntity<?> searchStudy(StudySearchCondition studySearchCondition){
        ResponseListDto<StudyDto> response = new ResponseListDto<StudyDto>();
        try {
            List<StudyDto> studyDtoList = new ArrayList<>();
            List<Study> studyList = studyRepository.findStudyByFilter(studySearchCondition);

            for (Study study : studyList) {
                StudyDto studyDto = studyDtoMapper(study);
                studyDtoList.add(studyDto);
            }
            response.setStatus(Status.SUCCESS);
            response.setData(studyDtoList);
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }



    }


    public ResponseEntity<?> applyPosition(String applyEmail, PositionApplyDto positionApplyDto){

        ResponseDto<String> response = new ResponseDto<>();
        try {
            StudyPosition studyPosition = studyRepository.findStudyPositionByName(positionApplyDto.getStudyId(), positionApplyDto.getPosition());
            Member member = memberDataRepository.findMemberByEmail(applyEmail);
            if (studyPosition.getTotalCount() <= studyPosition.getCount() || studyRepository.checkApplyStudy(member) || studyRepository.checkParticipateStudy(applyEmail) ) {
                throw new Exception("지원이 불가능한 상태입니다");
            }

            Study study = studyDataRepository.findById(positionApplyDto.getStudyId()).get();

            Position position = studyRepository.findPositionByPositionName(positionApplyDto.getPosition());

            StudyApplication studyApplication = StudyApplication.createStudyApplication(study, member, position, positionApplyDto.getMessage());
            if (study==null || member ==null || position ==null || studyApplication == null) {
                throw new IllegalArgumentException();
            }

            response.setStatus(Status.SUCCESS);
            response.setData("지원 성공");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }

    public List<AlarmDto> findStudyAlarm(String email) {
        List<StudyApplication> studyApplicationList = studyRepository.findStudyApplicationByEmail(email);
        List<AlarmDto> studyAlarmDtoList = alarmSetData(studyApplicationList);
        return studyAlarmDtoList;
    }

    private List<AlarmDto> alarmSetData(List<StudyApplication> studyApplicationList) {

        List<AlarmDto> result = studyApplicationList.stream().map(sa -> {
            AlarmDto alarmDto = new AlarmDto();
            alarmDto.setType(InviteType.STUDY);
            alarmDto.setId(sa.getId());
            alarmDto.setStudyId(sa.getStudy().getId());
            alarmDto.setEmail(sa.getStudy().getMember().getEmail());
            alarmDto.setEmail(sa.getMember().getEmail());
            alarmDto.setTitle(sa.getStudy().getTitle());
            alarmDto.setPosition(sa.getPosition().getPositionName());
            alarmDto.setMessage(sa.getMessage());
            alarmDto.setCreateDate(sa.getCreatedDate());
            alarmDto.setModifiedDate(sa.getModifiedDate());
            return alarmDto;
        }).collect(Collectors.toList());
        return result;
    }

    public ResponseEntity<?> findCreatedStudy(String email) {
        try {
            List<Study> studyByEmail = studyRepository.findCreatedStudyByEmail(email);
            List<StudyDto> studyDtoList = studyByEmail
                    .stream()
                    .map(this::studyDtoMapper)
                    .collect(Collectors.toList());

            ResponseListDto<StudyDto> response = new ResponseListDto<>();
            response.setStatus(Status.SUCCESS);
            response.setData(studyDtoList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }

    }


    public ResponseEntity<?> findParticipatedStudy(String email) {
        try {
            Member findMember = memberDataRepository.findMemberByEmail(email);
            List<Study> studyByEmail = studyRepository.findParticipatedStudyByEmail(findMember);
            List<StudyDto> studyDtoList = studyByEmail
                    .stream()
                    .map(this::studyDtoMapper)
                    .collect(Collectors.toList());

            ResponseListDto<StudyDto> response = new ResponseListDto<>();
            response.setStatus(Status.SUCCESS);
            response.setData(studyDtoList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }

    public ResponseEntity<?> joinStudy(StudyAllowDto studyAllowDto) {
        try {
            ResponseDto<String> response = new ResponseDto<>();
            if(!studyAllowDto.isAllow()){
                if (studyAllowDto.getType().equals(InviteType.STUDY)) {
                    studyRepository.deleteStudyApplicationById(studyAllowDto.getId());
                    response.setData("지원을 거절하였습니다.");
                }
                else{
                    memberDataRepository.deleteMemberInvitationById(studyAllowDto.getId());

                }
                response.setStatus(Status.SUCCESS);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            

            if (studyRepository.checkParticipateStudy(studyAllowDto.getEmail()) ) {
                throw new Exception("지원이 불가능한 상태입니다");
            }

            // 스터디 지원을 허락
            if (studyAllowDto.getType().equals(InviteType.STUDY)) {
                StudyApplication studyApplication = studyRepository.findStudyApplicationById(studyAllowDto.getId());
                Position position = studyApplication.getPosition();
//                String studyPositionName = position.getPositionName();
                return getResponseDtoToStudyAllow(studyAllowDto,position, response);
            }

            // 멤버 초대를 허락
            else{
                MemberInvitation memberInvitation = memberDataRepository.findMemberInvitationById(studyAllowDto.getId());
                Position position = memberInvitation.getPosition();
                return getResponseDtoToStudyAllow(studyAllowDto, position,response);
            }

        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }


    private ResponseEntity<ResponseDto<String>> getResponseDtoToStudyAllow(StudyAllowDto studyAllowDto,Position position, ResponseDto<String> response) {
//        StudyApplication studyApplication = studyRepository.findStudyApplicationById(studyAllowDto.getId());
//        Position position = studyApplication.getPosition();

        String studyPositionName = position.getPositionName();
        Member member = memberDataRepository.findMemberByEmail(studyAllowDto.getEmail());

        Study study = studyDataRepository.findById(studyAllowDto.getStudyId()).get();


        List<StudyPosition> studyPosition = study.getStudyPosition();

        studyPosition.stream()
                .filter(sp -> {
                    String positionName = sp.getPosition().getPositionName();
                    return positionName.equals(studyPositionName) && sp.getCount() < sp.getTotalCount();
                })
                .limit(1)
                .forEach(sa -> {
                    sa.setCount(sa.getCount() + 1);
                    StudyParticipation.createStudyParticipation(study, member, position);
                    if(studyAllowDto.getType().equals(InviteType.STUDY)){
                        studyRepository.deleteStudyApplicationById(studyAllowDto.getId());
                    }
                    else{
                        memberDataRepository.deleteMemberInvitationById(studyAllowDto.getId());
                    }
                });

        response.setStatus(Status.SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<?> getErrorResponseEntity(Exception e) {
        ResponseDto<String> errorResponse = new ResponseDto<String>();
        errorResponse.setStatus(Status.FALSE);
        errorResponse.setData(e.getMessage());
        return new ResponseEntity<ResponseDto>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}