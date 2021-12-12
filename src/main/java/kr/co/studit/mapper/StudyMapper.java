package kr.co.studit.mapper;

import kr.co.studit.dto.mapper.StudySearchDto;
import kr.co.studit.dto.mapper.StudyMapperDto;

import java.sql.SQLException;
import java.util.List;

public interface StudyMapper {

    List<StudyMapperDto> studyFilterSearch(StudySearchDto filterMap) throws SQLException;
}
