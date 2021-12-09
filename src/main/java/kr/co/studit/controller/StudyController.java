package kr.co.studit.controller;


import kr.co.studit.dto.StudyDto;
import kr.co.studit.entity.Study;
import kr.co.studit.service.StudyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")

public class StudyController {
    @Autowired
    StudyService studyService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("")
    public String index() {

        return "study";
    }

    @PostMapping("create")
    public ResponseEntity<StudyDto> AjaxCreate(@RequestBody StudyDto studyDto){

        StudyDto createStudy = studyService.createStudy(studyDto);

        if(createStudy==null){
            return new ResponseEntity<StudyDto>(createStudy, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<StudyDto>(studyDto, HttpStatus.CREATED);
    }
    @PostMapping("formCreate")
    public ResponseEntity<StudyDto> formCreate(StudyDto studyDto){

        StudyDto createStudy = studyService.createStudy(studyDto);

        if(createStudy==null){
            return new ResponseEntity<StudyDto>(createStudy, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<StudyDto>(studyDto, HttpStatus.CREATED);
    }
}
