package kr.co.studit.controller;


import kr.co.studit.dto.position.PositionApplyDto;
import kr.co.studit.dto.search.StudySearchCondition;
import kr.co.studit.dto.study.StudyAllowDto;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.dto.study.StudyUpdateDto;
import kr.co.studit.service.StudyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping()
    public ResponseEntity<?> studyList(Pageable pageable) {
        //?page=0&size=6 이런식으로 사용용
       return studyService.findStudies(pageable);
    }

    @PostMapping()
    public ResponseEntity<?> createStudy(@AuthenticationPrincipal String email,@RequestBody StudyDto studyDto){
        return studyService.createStudy(studyDto,email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findStudy(@PathVariable Long id) {
        return studyService.findStudy(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStudy(@PathVariable Long id, @RequestBody StudyUpdateDto studyUpdateDto, @AuthenticationPrincipal String email) {
        return studyService.updateStudy(id, email, studyUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(@AuthenticationPrincipal String email,@PathVariable Long id) {
        return studyService.deleteStudy(id,email);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchStudy(@RequestBody StudySearchCondition studySearchCondition,Pageable pageable) {
        return studyService.searchStudy(studySearchCondition,pageable);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyPosition(@AuthenticationPrincipal String email, @RequestBody PositionApplyDto positionApplyDto) {
        return studyService.applyPosition(email,positionApplyDto);
    }

    @PostMapping("/allow")
    public ResponseEntity<?> allowStudy(@RequestBody StudyAllowDto studyAllowDto) {
        return studyService.joinStudy(studyAllowDto);
    }

    @GetMapping("/created")
    public ResponseEntity<?> createdStudy(@AuthenticationPrincipal String email,Pageable pageable) {
        return studyService.findCreatedStudy(email,pageable);
    }

    @GetMapping("/participated")
    public ResponseEntity<?> participatedStudy(@AuthenticationPrincipal String email,Pageable pageable) {
        return studyService.findParticipatedStudy(email,pageable);
    }
}
