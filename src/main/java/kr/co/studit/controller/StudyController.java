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
    public ResponseEntity<ResponseListDto<StudyDto>> studyList() {

        List<StudyDto> studies = studyService.findStudies();
        ResponseListDto<StudyDto> response = new ResponseListDto<>();
        response.setStatus("success");
        response.setData(studies);

        return new ResponseEntity<ResponseListDto<StudyDto>>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ResponseDto> AjaxCreate(@RequestBody StudyDto studyDto){

        ResponseDto<StudyDto> response = new ResponseDto<>();

        try {
            StudyDto createStudy = studyService.createStudy(studyDto);
            response.setData(createStudy);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.setStatus("false");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ResponseDto> findStudy(@PathVariable Long id) {
        ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
        try {
            StudyDto studyDto = studyService.findStudy(id);

            response.setStatus("success");
            response.setData(studyDto);
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus("false");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> editStudy(@PathVariable Long id,@RequestBody StudyUpdateDto studyUpdateDto) {
        ResponseDto<String> response = new ResponseDto<String>();
        try {
            studyService.updateStudy(id, studyUpdateDto);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus("false");
            response.setData(e.getMessage());
            return new ResponseEntity<ResponseDto>(response, HttpStatus.BAD_REQUEST);
        }


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteStudy(@PathVariable Long id) {
        ResponseDto<String> response = new ResponseDto<String>();
        try {
            studyService.deleteStudy(id);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus("false");
            response.setData(e.getMessage());
            return new ResponseEntity<ResponseDto>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/search")
    public ResponseEntity<ResponseListDto> studySearch(@RequestBody StudySearchDto searchDto) {

        ResponseListDto<StudyDto> response = new ResponseListDto<StudyDto>();
        try {
            List<StudyDto> studyDtoList = studyService.searchStudy(searchDto);
            response.setStatus("success");
            response.setData(studyDtoList);
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.OK);
        } catch (Exception e) {
//            response.setStatus("false");
            response.setStatus(e.getMessage());
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/position")
    public ResponseEntity<ResponseDto> positionApply(@RequestBody PositionApplyDto positionApplyDto) {
        ResponseDto<StudyDto> response = new ResponseDto<StudyDto>();
        try {
            studyService.positionApply(positionApplyDto);
            response.setStatus("success");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
//            response.setStatus("false");
            response.setStatus(e.getMessage());
            return new ResponseEntity<ResponseDto>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/alarm")
    public ResponseEntity<ResponseListDto> studyApplication(@RequestBody String email) {
        ResponseListDto<StudyAlarmDto> response = new ResponseListDto<StudyAlarmDto>();
        try {
            List<StudyAlarmDto> studyAlarmDtoList = studyService.studyAlarm(email);
            response.setStatus("success");
            response.setData(studyAlarmDtoList);
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.OK);
        } catch (Exception e) {
//            response.setStatus("false");
            response.setStatus(e.getMessage());
            return new ResponseEntity<ResponseListDto>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
