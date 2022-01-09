package kr.co.studit.controller;


import kr.co.studit.dto.*;
import kr.co.studit.dto.mapper.StudySearchDto;
import kr.co.studit.service.StudyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")

public class StudyController {
    @Autowired
    StudyService studyService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/")
    public ResponseEntity<?> studyList() {
        try {
            ResponseListDto<StudyDto> response = new ResponseListDto<>();
            List<StudyDto> studies = studyService.findStudies();
            response.setStatus("success");
            response.setData(studies);

            return new ResponseEntity<ResponseListDto<StudyDto>>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }

    }

    @PostMapping()
    public ResponseEntity<?> AjaxCreate(@RequestBody StudyDto studyDto){

        try {
            ResponseDto<StudyDto> response = new ResponseDto<>();
            StudyDto createStudy = studyService.createStudy(studyDto);
            response.setData(createStudy);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }


    }
    @PostMapping("formCreate")
    public ResponseEntity<StudyDto> formCreate(StudyDto studyDto){

        StudyDto createStudy = studyService.createStudy(studyDto);

        if(createStudy==null){
            return new ResponseEntity<StudyDto>(createStudy, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<StudyDto>(studyDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findStudy(@PathVariable Long id) {

        try {
            ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
            StudyDto studyDto = studyService.findStudy(id);
            response.setStatus("success");
            response.setData(studyDto);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStudy(@PathVariable Long id,@RequestBody StudyUpdateDto studyUpdateDto) {
        try {
            ResponseDto<String> response = new ResponseDto<String>();
            studyService.updateStudy(id, studyUpdateDto);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(@PathVariable Long id) {
        try {
            ResponseDto<String> response = new ResponseDto<String>();
            studyService.deleteStudy(id);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchStudy(@RequestBody StudySearchDto searchDto) {

        ResponseListDto<StudyDto> response = new ResponseListDto<StudyDto>();
        try {
            List<StudyDto> studyDtoList = studyService.searchStudy(searchDto);
            response.setStatus("success");
            response.setData(studyDtoList);
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }



    @PostMapping("/position")
    public ResponseEntity<?> applyPosition(@RequestBody PositionApplyDto positionApplyDto) {

        try {
            ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
            studyService.positionApply(positionApplyDto);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }

    @PostMapping("/alarm")
    public ResponseEntity<?> applyStudy(@RequestBody EmailDto emailDto) {
        try {
            ResponseListDto<AlarmDto> response = new ResponseListDto<>();
            List<AlarmDto> studyAlarmDtoList = studyService.studyAlarm(emailDto.getEmail());
            response.setStatus("success");
            response.setData(studyAlarmDtoList);
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponseEntity(e);
        }
    }

    @PostMapping("/allow")
    public ResponseEntity<?> allowStudy(@RequestBody StudyAllowDto studyAllowDto) {
        ResponseEntity<?> response = studyService.joinStudy(studyAllowDto);
        return response;
    }

    @PostMapping("/created")
    public ResponseEntity<?> createdStudy(@RequestBody EmailDto emailDto) {
        ResponseEntity<?> studyByEmail = studyService.findCreatedStudy(emailDto.getEmail());
        return studyByEmail;
    }
    @PostMapping("/participated")
    public ResponseEntity<?> participatedStudy(@RequestBody EmailDto emailDto) {
        ResponseEntity<?> studyByEmail = studyService.findParticipatedStudy(emailDto.getEmail());
        return studyByEmail;
    }
    private ResponseEntity<?> getErrorResponseEntity(Exception e) {
        ResponseDto<String> errorResponse = new ResponseDto<String>();
        errorResponse.setStatus("false");
        errorResponse.setData(e.getMessage());
        return new ResponseEntity<ResponseDto>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
