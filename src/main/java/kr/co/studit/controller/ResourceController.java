package kr.co.studit.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.studit.dto.SkillDto;
import kr.co.studit.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/position")
    @ApiOperation(value = "포지션 리스트")
    public ResponseEntity<?> positionList() {
        return resourceService.findPositionList();
    }

    @GetMapping("/skill/{positionName}")
    @ApiOperation(value = "스킬 리스트")
    public ResponseEntity<?> skillList(@PathVariable String positionName) {
        return resourceService.findSkillList(positionName);
    }

    @GetMapping("/skills")
    @ApiOperation(value = "모든 스킬 리스트")
    public ResponseEntity<?> allSkillList() {
        List<String> allSkillList = resourceService.findAllSkillList();
        List<SkillDto> skillDtos = new ArrayList<>();
        for (String name : allSkillList) {
            SkillDto skillDto = new SkillDto(name);
            skillDtos.add(skillDto);
        }

        return ResponseEntity.ok().body(skillDtos);
    }
}
