package kr.co.studit.alarm;

import lombok.Data;

import java.util.List;

@Data
public class TotalAlarmDto {
    List<AlarmDto> studyAlarm;
    List<AlarmDto> memberAlarm;

    public static TotalAlarmDto createTotalAlarmDto(List<AlarmDto> studyAlarm, List<AlarmDto> memberAlarm) {
        TotalAlarmDto totalAlarmDto = new TotalAlarmDto();
        totalAlarmDto.setStudyAlarm(studyAlarm);
        totalAlarmDto.setMemberAlarm(memberAlarm);
        return totalAlarmDto;
    }
}
