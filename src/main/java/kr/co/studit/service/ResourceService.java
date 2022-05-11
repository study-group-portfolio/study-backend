package kr.co.studit.service;

import kr.co.studit.dto.SkillDto;
import kr.co.studit.response.Status;
import kr.co.studit.study.dto.PositionTypeDto;
import kr.co.studit.response.ResponseListDto;
import kr.co.studit.skill.Skill;
import kr.co.studit.position.domain.PositionType;
import kr.co.studit.response.ErrorResponse;
import kr.co.studit.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResponseEntity<?> findPositionList() {
        try {
            List<PositionType> positionTypeList = resourceRepository.findPositionList();
            List<PositionTypeDto> result = positionTypeList.stream()
                    .map(positionType -> {
                        PositionTypeDto positionTypeDto = new PositionTypeDto(positionType.getPositionTypeName());//                        List<String> positionList = new ArrayList<>();
                        List<String> positionList = positionType.getPosition()
                                .stream()
                                .map(position -> position.getPositionName())
                                .collect(Collectors.toList());
                        positionTypeDto.setPositionNameList(positionList);
                        return positionTypeDto;
                    })
                    .collect(Collectors.toList());


//            List<PositionSimpleDto> result = positionList
//                    .stream()
//                    .map(position -> new PositionSimpleDto(position.getPositionName()))
//                    .collect(Collectors.toList());
            ResponseListDto response = new ResponseListDto(Status.SUCCESS, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }
    public ResponseEntity<?> findSkillList(String positionName) {
        try {
            List<Skill> skillList = resourceRepository.findSkillByPositionName(positionName);
            List<SkillDto> result = skillList
                    .stream()
                    .map(skill -> new SkillDto(skill.getSkillName()))
                    .collect(Collectors.toList());
            ResponseListDto response = new ResponseListDto(Status.SUCCESS, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }
    }

    public List<String> findAllSkillList() {
        return resourceRepository.findAllSkills();
    }
}
