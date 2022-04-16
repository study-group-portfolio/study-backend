package kr.co.studit.service;

import kr.co.studit.dto.AlarmDto;
import kr.co.studit.dto.bookmark.BookmarkRes;
import kr.co.studit.dto.enums.InviteType;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.position.PositionApplyDto;
import kr.co.studit.dto.position.PositionDto;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.StudySearchCondition;
import kr.co.studit.dto.study.*;
import kr.co.studit.entity.Bookmark;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.Tool;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.member.MemberInvitation;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.study.*;
import kr.co.studit.error.ErrorResponse;
import kr.co.studit.repository.bookmark.BookmarkDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.repository.study.StudyDataRepository;
import kr.co.studit.repository.study.StudyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class StudyService {

    private final StudyDataRepository studyDataRepository;
    private final StudyRepository studyRepository;
    private final MemberDataRepository memberDataRepository;
    private final BookmarkDataRepository bookmarkDataRepository;

    public ResponseEntity<?> createStudy(StudyDto studyDto, String email) {
        ResponseDto<StudyDto> response = new ResponseDto<>();
        try {
            Study study = studyMapper(studyDto,email);
            Study saveStudy = studyDataRepository.save(study);
//            StudyDto saveStudyDto = studyDtoMapper(saveStudy);
            StudyDto saveStudyDto = getStudyDto(saveStudy);
            response.setData(saveStudyDto);
            response.setStatus(Status.SUCCESS);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }



    public ResponseEntity<?> findStudies(Pageable pageable) {
        ResponseDto<Page> response = new ResponseDto<>();

        try {
            Page<Study> studyPage = studyRepository.findStudy(pageable);
            List<StudyDto> result = studyPage.getContent().stream()
                    .map(this::getStudyDto)
                    .collect(Collectors.toList());
            response.setStatus(Status.SUCCESS);
            response.setData(new PageImpl<>(result,pageable,studyPage.getTotalElements()));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }

    public ResponseEntity<?> findStudiesWithBookmark(Pageable pageable, Long loginMemberId) {
        ResponseDto<Page> response = new ResponseDto<>();
        try {
            Page<Study> studyPage = studyRepository.findStudy(pageable);
            List<Study> studyList = studyPage.getContent();
            List<StudyDto> studyDtos = new ArrayList<>();

            for (Study study : studyList) {
                BookmarkRes bookmarkRes = getStudyBookmark(study, loginMemberId);
                StudyDto studyDto = getStudyDto(study);
                studyDto.setBookmarkId(bookmarkRes.getBookmarkId());
                studyDto.setBookmarkState(Optional.ofNullable(bookmarkRes.getBookmarkState()).isPresent() );
                studyDtos.add(studyDto);
            }
            response.setData(new PageImpl<>(studyDtos, pageable, studyPage.getTotalElements()));
            response.setStatus(Status.SUCCESS);

        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }


        return ResponseEntity.ok(response);

    }

    public BookmarkRes getStudyBookmark(Study study, Long loginMemberId) {
        Bookmark markeStudy = bookmarkDataRepository.findMarkeStudy(study.getId(), loginMemberId);
        BookmarkRes bookmarkRes = new BookmarkRes();
        if (markeStudy != null) {

            bookmarkRes.setBookmarkId(markeStudy.getId());
            bookmarkRes.setBookmarkState(true);
            return bookmarkRes;
        }

        return bookmarkRes;
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



    public ResponseEntity<?> findStudy(Long id){
        ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
        try {
            Study study = studyDataRepository.findById(id).orElseThrow(() -> new Exception("존재하지 않는 스터디 입니다"));

//            StudyDto studyDto = studyDtoMapper(study);
            StudyDto studyDto = getStudyDto(study);
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
            Study study = studyRepository.findStudyByEmailAndId(id,email).orElseThrow(() -> new Exception("사용자가 스터디를 생성하지 않았습니다"));
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
            Study study = studyRepository.findStudyByEmailAndId(id,email).orElseThrow(() -> new Exception("사용자가 스터디를 생성하지 않았습니다"));

            setStudyData(study, studyUpdateDto, true);

            studyDataRepository.save(study);

            response.setStatus(Status.SUCCESS);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }


    public ResponseEntity<?> searchStudy(StudySearchCondition studySearchCondition,Pageable pageable){
        ResponseDto<PageImpl> response = new ResponseDto<>();
        try {
            Page<Study> findStudis = studyRepository.findStudyByFilter(studySearchCondition,pageable);
            List<StudyDto> result = findStudis.getContent().stream()
                    .map(this::getStudyDto)
                    .collect(Collectors.toList());
            response.setStatus(Status.SUCCESS);
            response.setData(new PageImpl<>(result,pageable,findStudis.getSize()));
            return new ResponseEntity<>(response, HttpStatus.OK);

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

    public ResponseEntity<?> findCreatedStudy(String email,Pageable pageable) {
        ResponseDto<Page> response = new ResponseDto<>();
        try {
            Page<Study> studyPage = studyRepository.findCreatedStudyByEmail(email, pageable);

            List<StudyDto> result = studyPage.getContent().stream()
                    .map(this::getStudyDto)
                    .collect(Collectors.toList());
            response.setStatus(Status.SUCCESS);
            response.setData(new PageImpl<>(result,pageable,studyPage.getTotalElements()));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }

    }


    public ResponseEntity<?> findParticipatedStudy(String email,Pageable pageable) {
        ResponseDto<Page> response = new ResponseDto<>();
        try {
            Page<Study> studyPage = studyRepository.findParticipatedStudyByEmail(email,pageable);
            List<Study> studyByEmail = studyPage.getContent();
            List<StudyDto> result = studyByEmail
                    .stream()
                    .map(this::getStudyDto)
                    .collect(Collectors.toList());

            response.setStatus(Status.SUCCESS);
            response.setData(new PageImpl<>(result,pageable,studyPage.getTotalElements()));
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
    public ResponseEntity<?> findCountPositionStudy(Long id) {
        try {
            List<StudyPosition> studyPositionList = studyRepository.findStudyPositionByStudyId(id);
            StudyPositionDto studyPositionDto = new StudyPositionDto();
            List<PositionDto> result = studyPositionList
                    .stream()
                    .map(studyPosition -> {
                        PositionDto positionDto = new PositionDto();
                        positionDto.setPositionName(studyPosition.getPosition().getPositionName());
                        positionDto.setTotalCount(studyPosition.getTotalCount());
                        positionDto.setCount(studyPosition.getCount());
                        positionDto.setRecruited(studyPosition.isRecruited());
                        return positionDto;
                    })
                    .collect(Collectors.toList());
            studyPositionDto.setStudyId(id);
            studyPositionDto.setPositions(result);
            ResponseDto<StudyPositionDto> response = new ResponseDto<>();
            response.setData(studyPositionDto);
            response.setStatus(Status.SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }



    private void createPositionSkill(Study study, ArrayList<PositionDto> positions) {
        for (PositionDto positionDto : positions) {

            Position position = studyRepository.findPositionByPositionName(positionDto.getPositionName());

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


    public StudyDto getStudyDto(Study study) {
        StudyDto studyDto = study.toStudyDto();

        List<StudyPosition> studyPositions = study.getStudyPosition();
        for (StudyPosition studyPosition : studyPositions) {
            PositionDto positionDto = new PositionDto();

            Position position = studyPosition.getPosition();
            positionDto.setPositionName(position.getPositionName());
            positionDto.setCount(studyPosition.getCount());
            positionDto.setTotalCount(studyPosition.getTotalCount());
            positionDto.setRecruited(studyPosition.isRecruited());

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
        Member member = memberDataRepository.findMemberByEmail(email);
        study.setMember(member);
        setStudyData(study,studyDto,false);
        return study;
    }

    private void setStudyData(Study study, StudyForm studyDto, boolean update) {
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


        study.setReceptionStart(LocalDate.parse(studyDto.getReceptionStart()) );
        study.setReceptionEnd(LocalDate.parse(studyDto.getReceptionEnd()));

        study.setStudySkill(new ArrayList<StudySkill>());
        study.setStudyPosition(new ArrayList<StudyPosition>());
        createStudyTools(study, studyDto.getTools());
        createPositionSkill(study, studyDto.getPositions());


    }


}