package kr.co.studit.controller;


import io.swagger.annotations.ApiOperation;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.position.PositionApplyDto;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.StudySearchCondition;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.dto.study.StudyUpdateDto;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.service.BookmarkService;
import kr.co.studit.service.StudyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;
    private final MemberDataRepository memberDataRepository;

    @GetMapping()
    @ApiOperation(value = "스터디 글 리스트 조회", notes ="pageNumber와 pageSize를 통해 페이징 가능, 페이지는 0페이지 부터 시작 ex) ?page=0&size=6 이런식으로 사용용")
    public ResponseEntity<?> studyList(@AuthenticationPrincipal String email, @PageableDefault(size = 12) Pageable pageable) {
        if( !email.equals("anonymousUser")){
            Long id = memberDataRepository.findMemberByEmail(email).getId();
            return studyService.findStudiesWithBookmark(pageable, id);
        }
       return studyService.findStudies(pageable);
    }

    @PostMapping()
    @ApiOperation(value = "스터디 글 생성")
    public ResponseEntity<?> createStudy(@AuthenticationPrincipal String email, @RequestBody StudyDto studyDto){
        return studyService.createStudy(studyDto,email);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "스터디 글 조회")
    public ResponseEntity<?> findStudy(@PathVariable Long id) {
        //TODO 로그인한 멤버가 조회시 북마크 상태가 나와야한다,
        return studyService.findStudy(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "스터디 글 수정")
    public ResponseEntity<?> editStudy(@PathVariable Long id, @RequestBody StudyUpdateDto studyUpdateDto, @AuthenticationPrincipal String email) {
        return studyService.updateStudy(id, email, studyUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "스터디 글 삭제")
    public ResponseEntity<?> deleteStudy(@AuthenticationPrincipal String email,@PathVariable Long id) {
        return studyService.deleteStudy(id,email);
    }

    @PostMapping("/search")
    @ApiOperation(value = "스터디 글 검색", notes ="pageNumber와 pageSize를 통해 페이징 가능, 페이지는 0페이지 부터 시작")
    public ResponseEntity<?> searchStudy(@RequestBody StudySearchCondition studySearchCondition,Pageable pageable) {
        return studyService.searchStudy(studySearchCondition,pageable);
    }

    @PostMapping("/apply")
    @ApiOperation(value = "스터디 지원")
    public ResponseEntity<?> applyPosition(@AuthenticationPrincipal String email, @RequestBody PositionApplyDto positionApplyDto) {
        return studyService.applyPosition(email,positionApplyDto);
    }


    @GetMapping("/created")
    @ApiOperation(value = "내가 생성한 스터디", notes ="pageNumber와 pageSize를 통해 페이징 가능, 페이지는 0페이지 부터 시작")
    public ResponseEntity<?> createdStudy(@AuthenticationPrincipal String email,Pageable pageable) {
        return studyService.findCreatedStudy(email,pageable);
    }

    @GetMapping("/participated")
    @ApiOperation(value = "내가 참여한 스터디", notes ="pageNumber와 pageSize를 통해 페이징 가능, 페이지는 0페이지 부터 시작")
    public ResponseEntity<?> participatedStudy(@AuthenticationPrincipal String email,Pageable pageable) {
        return studyService.findParticipatedStudy(email,pageable);
    }

    @GetMapping("/position/{id}")
    @ApiOperation(value = "스터디 포지션 정보")
    public ResponseEntity<?> countPositionStudy(@PathVariable Long id) {
        return studyService.findCountPositionStudy(id);
    }
}
