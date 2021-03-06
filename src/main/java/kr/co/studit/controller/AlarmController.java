package kr.co.studit.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.studit.dto.*;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.study.StudyAllowDto;
import kr.co.studit.error.ErrorResponse;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.service.MemberService;
import kr.co.studit.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final MemberService memberService;
    private final MemberDataRepository memberDataRepository;
    private final StudyService studyService;

    @GetMapping()
    @ApiOperation(value = "알람 리스트")
    public ResponseEntity<?> alarm(@AuthenticationPrincipal String email) {
        try {
            List<AlarmDto> studyAlarm = studyService.findStudyAlarm(email);
            List<AlarmDto> memberAlarm = memberService.findMemberAlarm(email);

            ResponseDto<TotalAlarmDto> response = new ResponseDto<>();
            TotalAlarmDto totalAlarmDto = TotalAlarmDto.createTotalAlarmDto(studyAlarm, memberAlarm);
            response.setStatus(Status.SUCCESS);
            response.setData(totalAlarmDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }

    }


    @PostMapping("/allow")
    @ApiOperation(value = "내가 생성한 스터디")
    public ResponseEntity<?> allowStudy(@RequestBody StudyAllowDto studyAllowDto) {
        ResponseEntity<?> response = studyService.joinStudy(studyAllowDto);
        return response;
    }
}
