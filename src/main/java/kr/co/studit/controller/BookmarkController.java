package kr.co.studit.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.CustomPage;
import kr.co.studit.dto.study.StudyDto;
import kr.co.studit.dto.bookmark.BookmarkRes;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.member.SearchMemberDto;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.study.Study;
import kr.co.studit.repository.study.StudyDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final MemberDataRepository memberDataRepository;
    private final StudyDataRepository studyDataRepository;
    private final BookmarkService bookmarkService;

    //  본인이 만든 스터디는 북마크 할 수 없다.
    @ApiOperation(value = "회원 북마크 추가")
    @PostMapping("/member/{markedMemberId}")
    public ResponseEntity<?> createMemberBookmark(@AuthenticationPrincipal String email, @PathVariable Long markedMemberId) {
        // 벨리데이션 체크 ? 로그인한 사용자가 해당 유저의 북마크 요청을 두번 하였을 경우 ? 무결성 제약조건에 걸려서 에러 발생..
        Member markMember = memberDataRepository.findMemberByEmail(email);
        Member markedMember = memberDataRepository.findMemberById(markedMemberId);
        BookmarkRes res = bookmarkService.createMemberBookmark(markMember, markedMember);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(res)
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "북마크 삭제", notes = "북마크id를 통해 북마크(회원,스터디) 삭제")
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<?> deleteMemberBookmark(@AuthenticationPrincipal String email, @PathVariable Long bookmarkId) {
        BookmarkRes res = bookmarkService.deleteMemberBookmark(bookmarkId);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(res)
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "북마크 회원 리스트 조회")
    @GetMapping("/member/list")
    public ResponseEntity<?> findMemberBookmarkList(@AuthenticationPrincipal String email, @PageableDefault(page = 0, size = 4) Pageable pageable) {
        Member member = memberDataRepository.findMemberByEmail(email);
        Long memberId = member.getId();
        CustomPage<SearchMemberDto> memberBookmarkList = bookmarkService.findMemberBookmarkList(memberId, pageable);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(memberBookmarkList)
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "북마크 스터디 추가")
    @PostMapping("/study/{markedStudyId}")
    public ResponseEntity<?> createStudyBookmark(@AuthenticationPrincipal String email, @PathVariable Long markedStudyId) {

        Member markMember = memberDataRepository.findMemberByEmail(email);
        Study markedStudy = studyDataRepository.findById(markedStudyId).get();
        if (markMember.getId() == markedStudy.getMember().getId()) {
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .status(Status.FALSE)
                    .message("본인이 생성한 스터디는 북마크 할 수 없습니다.")
                    .build();
            return ResponseEntity.ok(responseDto);
        }

        BookmarkRes bookmarkRes = bookmarkService.createStudyBookmark(markMember, markedStudy);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(bookmarkRes)
                .build();
        return ResponseEntity.ok(responseDto);
    }


    @ApiOperation(value = "북마크 스터디 리스트 조회")
    @GetMapping("/study/list")
    public ResponseEntity<?> findStudyBookmarkList(@AuthenticationPrincipal String email, @PageableDefault(page = 0, size = 4) Pageable pageable) {
        Member member = memberDataRepository.findMemberByEmail(email);
        Long memberId = member.getId();
        Page<StudyDto> studyBookmarkList = bookmarkService.findStudyBookmarkList(memberId, pageable);

        ResponseDto<Object> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(studyBookmarkList)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
